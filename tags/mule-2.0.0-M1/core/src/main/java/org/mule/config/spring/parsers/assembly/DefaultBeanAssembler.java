/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.config.spring.parsers.assembly;

import org.mule.config.spring.parsers.collection.ChildListEntryDefinitionParser;
import org.mule.config.spring.parsers.collection.ChildMapEntryDefinitionParser;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.w3c.dom.Attr;

public class DefaultBeanAssembler implements BeanAssembler
{

    private PropertyConfiguration beanConfig;
    private BeanDefinitionBuilder bean;
    private PropertyConfiguration targetConfig;
    private BeanDefinition target;

    public DefaultBeanAssembler(PropertyConfiguration beanConfig, BeanDefinitionBuilder bean,
                             PropertyConfiguration targetConfig, BeanDefinition target)
    {
        this.beanConfig = beanConfig;
        this.bean = bean;
        this.targetConfig = targetConfig;
        this.target = target;
    }

    public BeanDefinitionBuilder getBean()
    {
        return bean;
    }

    public BeanDefinition getTarget()
    {
        return target;
    }

    /**
     * Add a property defined by an attribute to the bean we are constructing.
     *
     * <p>Since an attribute value is always a string, we don't have to deal with complex types
     * here - the only issue is whether or not we have a reference.  References are detected
     * by explicit annotation or by the "-ref" at the end of an attribute name.  We do not
     * check the Spring repo to see if a name already exists since that could lead to
     * unpredictable behaviour.
     * (see {@link org.mule.config.spring.parsers.assembly.PropertyConfiguration})
     * @param attribute The attribute to add
     */
    public void extendBean(Attr attribute)
    {
        String oldName = attributeName(attribute);
        String oldValue = attribute.getNodeValue();
        String newName = beanConfig.bestGuessName(oldName, bean.getBeanDefinition().getBeanClassName());
        String newValue = beanConfig.translateValue(oldName, oldValue);
        if (!beanConfig.isIgnored(oldName))
        {
            extendBean(newName, newValue, beanConfig.isBeanReference(oldName));
        }
    }

    /**
     * Allow direct access to target for major hacks
     *
     * @param newName The property name to add
     * @param newValue The property value to add
     * @param isReference If true, a bean reference is added (and newValue must be a String)
     */
    public void extendBean(String newName, Object newValue, boolean isReference)
    {
        if (isReference)
        {
            if (newValue instanceof String)
            {
                bean.addPropertyReference(newName, (String) newValue);
            }
            else
            {
                throw new IllegalArgumentException("Bean reference must be a String: " + newName + "/" + newValue);
            }
        }
        else
        {
            bean.addPropertyValue(newName, newValue);
        }
    }

    /**
     * Add a property defined by an attribute to the parent of the bean we are constructing.
     *
     * <p>This is unusual.  Normally you want {@link #extendBean(org.w3c.dom.Attr)}.
     * @param attribute The attribute to add
     */
    public void extendTarget(Attr attribute)
    {
        String oldName = attributeName(attribute);
        String oldValue = attribute.getNodeValue();
        String newName = targetConfig.bestGuessName(oldName, bean.getBeanDefinition().getBeanClassName());
        String newValue = targetConfig.translateValue(oldName, oldValue);
        if (!targetConfig.isIgnored(oldName))
        {
            extendTarget(newName, newValue, targetConfig.isBeanReference(oldName));
        }
    }

    /**
     * Allow direct access to target for major hacks
     *
     * @param newName The property name to add
     * @param newValue The property value to add
     * @param isReference If true, a bean reference is added (and newValue must be a String)
     */
    public void extendTarget(String newName, Object newValue, boolean isReference)
    {
        assertTargetPresent();
        if (isReference)
        {
            if (newValue instanceof String)
            {
                target.getPropertyValues().addPropertyValue(newName, new RuntimeBeanReference((String) newValue));
            }
            else
            {
                throw new IllegalArgumentException("Bean reference must be a String: " + newName + "/" + newValue);
            }
        }
        else
        {
            target.getPropertyValues().addPropertyValue(newName, newValue);
        }
    }

    /**
     * Insert the bean we have built into the target (typically the parent bean).
     *
     * <p>This is the most complex case because the bean can have an aribtrary type.
     * @param oldName The identifying the bean (typically element name).
     */
    public void insertBeanInTarget(String oldName)
    {
        assertTargetPresent();
        String newName = targetConfig.bestGuessName(oldName, target.getBeanClassName());
        Object source = bean.getBeanDefinition().getSource();
        PropertyValue pv = target.getPropertyValues().getPropertyValue(newName);
        if (! targetConfig.isIgnored(oldName))
        {
            if (source instanceof ChildMapEntryDefinitionParser.KeyValuePair)
            {
                if (pv == null)
                {
                    pv = new PropertyValue(newName, new ManagedMap());
                }
                ChildMapEntryDefinitionParser.KeyValuePair pair = (ChildMapEntryDefinitionParser.KeyValuePair) source;
                ((Map) pv.getValue()).put(pair.getKey(), pair.getValue());
            }
            else if (targetConfig.isCollection(newName) ||
                    source instanceof ChildListEntryDefinitionParser.ListEntry)
            {
                if (pv == null)
                {
                    pv = new PropertyValue(newName, new ManagedList());
                }
                List list = (List) pv.getValue();
                if (source instanceof ChildListEntryDefinitionParser.ListEntry)
                {
                    ChildListEntryDefinitionParser.ListEntry entry = (ChildListEntryDefinitionParser.ListEntry) source;
                    list.add(entry.getProxiedObject());
                }
                else
                {
                    list.add(bean.getBeanDefinition());
                }
            }
            else
            {
                pv = new PropertyValue(newName, bean.getBeanDefinition());
            }
            target.getPropertyValues().addPropertyValue(pv);
        }
    }


    /**
     * Copy the properties from the bean we have been building into the target (typically
     * the parent bean).  In other words, the bean is a facade for the target.
     *
     * <p>This assumes that the source bean has been constructed correctly (ie the decisions about
     * what is ignored, a map, a list, etc) have already been made.   All it does (apart from a
     * direct copy) is merge collections with those on the target when necessary.
     */
    public void copyBeanToTarget()
    {
        assertTargetPresent();
        MutablePropertyValues targetProperties = target.getPropertyValues();
        MutablePropertyValues beanProperties = bean.getBeanDefinition().getPropertyValues();
        for (int i=0;i < beanProperties.size(); i++)
        {
            PropertyValue propertyValue = beanProperties.getPropertyValues()[i];
            String name = propertyValue.getName();
            Object value = propertyValue.getValue();
            Object oldValue = null;
            if (targetProperties.contains(name))
            {
                oldValue = targetProperties.getPropertyValue(name).getValue();
            }
            // merge collections
            if (targetConfig.isCollection(name) || oldValue instanceof Collection || value instanceof Collection)
            {
                Collection values = new ManagedList();
                if (null != oldValue)
                {
                    targetProperties.removePropertyValue(name);
                    if (oldValue instanceof Collection)
                    {
                        values.addAll((Collection) oldValue);
                    }
                    else
                    {
                        values.add(oldValue);
                    }
                }
                if (value instanceof Collection)
                {
                    values.addAll((Collection) value);
                }
                else
                {
                    values.add(value);
                }
                targetProperties.addPropertyValue(name, values);
            }
            else
            {
                targetProperties.addPropertyValue(name, value);
            }
        }
    }

    public static String attributeName(Attr attribute)
    {
        String name = attribute.getLocalName();
        if (null == name)
        {
            name = attribute.getName();
        }
        return name;
    }

    protected void assertTargetPresent()
    {
        if (null == target)
        {
            throw new IllegalStateException("Bean assembler does not have a target");
        }
    }

}

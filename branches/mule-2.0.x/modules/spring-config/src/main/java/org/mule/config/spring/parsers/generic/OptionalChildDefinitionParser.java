/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.config.spring.parsers.generic;

import org.mule.config.spring.parsers.assembly.BeanAssembler;
import org.mule.util.StringUtils;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * This class should be used when the same element can be configured as a child or an orphan 
 * (i.e., top-level).  It will inject the bean into the parent if the parent exists, otherwise 
 * it will not complain (ChildDefinitionParser throws an exception if no parent exists).
 */
public class OptionalChildDefinitionParser extends ChildDefinitionParser
{
    private boolean isChild;
    
    public OptionalChildDefinitionParser(String setterMethod)
    {
        super(setterMethod);
    }
    
    public OptionalChildDefinitionParser(String setterMethod, Class clazz)
    {
        super(setterMethod, clazz);
    }
    
    public OptionalChildDefinitionParser(String setterMethod, Class clazz, Class constraint)
    {
        super(setterMethod, clazz, constraint);
    }
    
    public OptionalChildDefinitionParser(String setterMethod, Class clazz, Class constraint, boolean allowClassAttribute)
    {
        super(setterMethod, clazz, constraint, allowClassAttribute);
    }

    @Override
    protected void parseChild(Element element, ParserContext parserContext, BeanDefinitionBuilder builder)
    {
        String parentBean = getParentBeanName(element);
        isChild = !(StringUtils.isBlank(parentBean));

        super.parseChild(element, parserContext, builder);
    }

    public BeanDefinition getParentBeanDefinition(Element element)
    {
        if (isChild)
        {
            return super.getParentBeanDefinition(element);
        }
        else
        {
            return null;
        }
    }

    protected void postProcess(ParserContext context, BeanAssembler assembler, Element element)
    {
        if (isChild)
        {
            super.postProcess(context, assembler, element);
        }
    }
}



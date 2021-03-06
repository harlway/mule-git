/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transformers;

import org.mule.RegistryContext;
import org.mule.config.i18n.CoreMessages;
import org.mule.providers.NullPayload;
import org.mule.registry.DeregistrationException;
import org.mule.registry.RegistrationException;
import org.mule.umo.UMOMessage;
import org.mule.umo.endpoint.UMOImmutableEndpoint;
import org.mule.umo.lifecycle.InitialisationException;
import org.mule.umo.lifecycle.Registerable;
import org.mule.umo.registry.RegistryFacade;
import org.mule.umo.transformer.TransformerException;
import org.mule.umo.transformer.UMOTransformer;
import org.mule.util.ClassUtils;
import org.mule.util.FileUtils;
import org.mule.util.StringMessageUtils;

import java.util.List;

import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>AbstractTransformer</code> is a base class for all transformers.
 * Transformations transform one object into another.
 */

public abstract class AbstractTransformer implements UMOTransformer
{
    protected static final int DEFAULT_TRUNCATE_LENGTH = 200;

    /**
     * logger used by this class
     */
    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * The return type that will be returned by the {@link #transform} method is
     * called
     */
    protected Class returnClass = null;

    /**
     * The name that identifies this transformer. If none is set the class name of
     * the transformer is used
     */
    protected String name = null;

    /**
     * The endpoint that this transformer instance is configured on
     */
    protected UMOImmutableEndpoint endpoint = null;

    /**
     * A list of supported Class types that the source payload passed into this
     * transformer
     */
    protected final List sourceTypes = new CopyOnWriteArrayList();

    /**
     * This is the following transformer in the chain of transformers.
     */
    protected UMOTransformer nextTransformer;

    /**
     * Determines whether the transformer will throw an exception if the message
     * passed is is not supported or the return tye is incorrect
     */
    private boolean ignoreBadInput = false;

    /**
     * Registry ID
     */
    protected String registryId = null;

    /**
     * default constructor required for discovery
     */
    public AbstractTransformer()
    {
        name = this.generateTransformerName();
    }

    protected Object checkReturnClass(Object object) throws TransformerException
    {
        if (returnClass != null)
        {
            if (!returnClass.isInstance(object))
            {
                throw new TransformerException(
                    CoreMessages.transformUnexpectedType(object.getClass(), returnClass),
                    this);
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("The transformed object is of expected type. Type is: " +
                    ClassUtils.getSimpleName(object.getClass()));
        }

        return object;
    }

    protected void registerSourceType(Class aClass)
    {
        if (!sourceTypes.contains(aClass))
        {
            sourceTypes.add(aClass);

            if (aClass.equals(Object.class))
            {
                logger.debug("java.lang.Object has been added as source type for this transformer, there will be no source type checking performed");
            }
        }
    }

    protected void unregisterSourceType(Class aClass)
    {
        sourceTypes.remove(aClass);
    }

    /**
     * @return transformer name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param string
     */
    public void setName(String string)
    {
        if (string == null)
        {
            string = ClassUtils.getSimpleName(this.getClass());
        }

        logger.debug("Setting transformer name to: " + string);
        name = string;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.transformers.Transformer#getReturnClass()
     */
    public Class getReturnClass()
    {
        return returnClass;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.transformers.Transformer#setReturnClass(java.lang.String)
     */
    public void setReturnClass(Class newClass)
    {
        returnClass = newClass;
    }

    public boolean isSourceTypeSupported(Class aClass)
    {
        return isSourceTypeSupported(aClass, false);
    }

    public boolean isSourceTypeSupported(Class aClass, boolean exactMatch)
    {
        int numTypes = sourceTypes.size();

        if (numTypes == 0)
        {
            return !exactMatch;
        }

        for (int i = 0; i < numTypes; i++)
        {
            Class anotherClass = (Class) sourceTypes.get(i);
            if (exactMatch)
            {
                if (anotherClass.equals(aClass))
                {
                    return true;
                }
            }
            else if (anotherClass.isAssignableFrom(aClass))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Transforms the object.
     * 
     * @param src The source object to transform.
     * @return The transformed object
     */
    public final Object transform(Object src) throws TransformerException
    {
        String encoding = null;

        if (src instanceof UMOMessage && !isSourceTypeSupported(UMOMessage.class))
        {
            encoding = ((UMOMessage) src).getEncoding();
            src = ((UMOMessage) src).getPayload();
        }

        if (encoding == null && endpoint != null)
        {
            encoding = endpoint.getEncoding();
        }
        else if (encoding == null)
        {
            encoding = FileUtils.DEFAULT_ENCODING;
        }

        if (!isSourceTypeSupported(src.getClass()))
        {
            if (ignoreBadInput)
            {
                logger.debug("Source type is incompatible with this transformer and property 'ignoreBadInput' is set to true, so the transformer chain will continue.");
                return nextTransform(src);
            }
            else
            {
                throw new TransformerException(
                    CoreMessages.transformOnObjectUnsupportedTypeOfEndpoint(this.getName(), 
                        src.getClass(), endpoint), this);
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Applying transformer " + getName() + " (" + getClass().getName() + ")");
            logger.debug("Object before transform: "
                            + StringMessageUtils.truncate(StringMessageUtils.toString(src), DEFAULT_TRUNCATE_LENGTH, false));
        }

        Object result = doTransform(src, encoding);
        if (result == null)
        {
            result = NullPayload.getInstance();
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Object after transform: "
                            + StringMessageUtils.truncate(StringMessageUtils.toString(result), DEFAULT_TRUNCATE_LENGTH, false));
        }

        result = checkReturnClass(result);

        result = nextTransform(result);

        return result;
    }

    public UMOImmutableEndpoint getEndpoint()
    {
        return endpoint;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.umo.transformer.UMOTransformer#setConnector(org.mule.umo.provider.UMOConnector)
     */
    public void setEndpoint(UMOImmutableEndpoint endpoint)
    {
        this.endpoint = endpoint;
        UMOTransformer trans = nextTransformer;
        while (trans != null && endpoint != null)
        {
            trans.setEndpoint(endpoint);
            trans = trans.getNextTransformer();
        }
    }

    protected abstract Object doTransform(Object src, String encoding) throws TransformerException;

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.umo.transformer.UMOTransformer#getNextTransformer()
     */
    public UMOTransformer getNextTransformer()
    {
        return nextTransformer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.umo.transformer.UMOTransformer#setNextTransformer(org.mule.umo.transformer.UMOTransformer)
     */
    public void setNextTransformer(UMOTransformer nextTransformer)
    {
        this.nextTransformer = nextTransformer;
    }

    /**
     * Will return the return type for the last transformer in the chain
     * 
     * @return the last transformers return type
     */
    public Class getFinalReturnClass()
    {
        UMOTransformer tempTrans = this;
        UMOTransformer returnTrans = this;
        while (tempTrans != null)
        {
            returnTrans = tempTrans;
            tempTrans = tempTrans.getNextTransformer();
        }
        return returnTrans.getReturnClass();
    }

    /**
     * Template method were deriving classes can do any initialisation after the
     * properties have been set on this transformer
     * 
     * @throws InitialisationException
     */
    public void initialise() throws InitialisationException
    {
        // nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.umo.lifecycle.Registerable#register()
     */
    public void register() throws RegistrationException
    {
        RegistryFacade registry = RegistryContext.getRegistry();
        if (registry == null) throw new RegistrationException("No registry available");
        Registerable parent = null;
        if (endpoint != null)
        {
            parent = endpoint;
        }
        else 
        {
            //TODO LM: what should the parent be
            parent = null;
        }

        registryId = registry.registerMuleObject(parent, this).getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.umo.lifecycle.Registerable#deregister()
     */
    public void deregister() throws DeregistrationException
    {
        RegistryContext.getRegistry().deregisterComponent(registryId);
        registryId = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.umo.lifecycle.Registerable#getRegistryId()
     */
    public String getRegistryId()
    {
        return registryId;
    }

    protected String generateTransformerName()
    {
        return ClassUtils.getSimpleName(this.getClass());
    }

    public List getSourceTypes()
    {
        return sourceTypes;
    }

    public boolean isIgnoreBadInput()
    {
        return ignoreBadInput;
    }

    public void setIgnoreBadInput(boolean ignoreBadInput)
    {
        this.ignoreBadInput = ignoreBadInput;
    }

    // @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer(80);
        sb.append(ClassUtils.getSimpleName(this.getClass()));
        sb.append("{this=").append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(", name='").append(name).append('\'');
        sb.append(", ignoreBadInput=").append(ignoreBadInput);
        sb.append(", returnClass=").append(returnClass);
        sb.append(", sourceTypes=").append(sourceTypes);
        sb.append('}');
        return sb.toString();                        
    }

    public boolean isAcceptNull()
    {
        return false;
    }

    /**
     * Safely call the next transformer in chain, if any.
     */
    protected Object nextTransform(Object result)
            throws TransformerException
    {
        if (nextTransformer != null)
        {
            logger.debug("Following transformer in the chain is " + nextTransformer.getName() + " ("
                            + nextTransformer.getClass().getName() + ")");
            result = nextTransformer.transform(result);
        }
        return result;
    }
}

/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.config.pool;

import org.mule.config.PoolingProfile;
import org.mule.impl.MuleDescriptor;
import org.mule.umo.UMOException;
import org.mule.umo.lifecycle.Disposable;
import org.mule.umo.lifecycle.Startable;
import org.mule.umo.lifecycle.Stoppable;
import org.mule.umo.model.UMOModel;
import org.mule.util.object.ObjectFactory;
import org.mule.util.object.ObjectPool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * <code>CommonsPoolProxyPool</code> is pool used to store MuleProxy objects. This
 * pool is a jakarta commons-pool implementation.
 */
public class CommonsPoolProxyPool implements ObjectPool
{
    /**
     * logger used by this class
     */
    protected static final Log logger = LogFactory.getLog(CommonsPoolProxyPool.class);

    /**
     * The pool that holds the MuleProxy objects
     */
    protected GenericObjectPool pool;

    /**
     * the factory used to create objects for the pool
     */
    protected ObjectFactory factory;

    private List components;

    /**
     * Creates a new pool and an Object factory with the UMODescriptor
     *
     * @param descriptor the descriptor to use when constructing MuleProxy objects in
     *            the pool
     */
    public CommonsPoolProxyPool(MuleDescriptor descriptor, UMOModel model, ObjectFactory factory, PoolingProfile pp)
        {
        this.factory = factory;

        GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.maxIdle = pp.getMaxIdle();
        config.maxActive = pp.getMaxActive();
        config.maxWait = pp.getMaxWait();
        config.whenExhaustedAction = (byte) pp.getExhaustedAction();

        init(descriptor, model, config);
    }

    /**
     * @param descriptor the UMO descriptor to pool
     * @param config the config to use when configuring the pool
     */
    public CommonsPoolProxyPool(MuleDescriptor descriptor, UMOModel model, GenericObjectPool.Config config)
    {
        init(descriptor, model, config);
    }

    /**
     * @param descriptor the UMO descriptor to pool
     * @param config the config to use when configuring the pool
     */
    private void init(MuleDescriptor descriptor, UMOModel model, GenericObjectPool.Config config)
    {
        components = new ArrayList();

        if (factory == null)
        {
            setFactory(new CommonsPoolProxyFactory(descriptor, model));
        }

        pool = new GenericObjectPool((PoolableObjectFactory) factory, config);

        if (factory instanceof CommonsPoolProxyFactory)
        {
            ((CommonsPoolProxyFactory) factory).setPool(this);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mule.model.pool.ObjectPool#borrowObject()
     */
    public Object borrowObject() throws Exception
    {
        return pool.borrowObject();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mule.model.pool.ObjectPool#returnObject(java.lang.Object)
     */
    public void returnObject(Object object) throws Exception
    {
        pool.returnObject(object);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mule.model.pool.ObjectPool#getSize()
     */
    public int getSize()
    {
        return pool.getNumActive();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mule.model.pool.ObjectPool#getMaxSize()
     */
    public int getMaxSize()
    {
        return pool.getMaxActive();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mule.model.pool.ObjectPool#setFactory(org.mule.model.pool.ProxyFactory)
     */
    public void setFactory(ObjectFactory factory)
    {
        this.factory = factory;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mule.model.pool.ObjectPool#clearPool()
     */
    public void clearPool()
    {
        synchronized (components)
        {
            for (Iterator i = components.iterator(); i.hasNext();)
            {
                ((Disposable) i.next()).dispose();
            }
            components.clear();
        }
        pool.clear();
    }

    public void onAdd(Object proxy)
    {
        synchronized (components)
        {
            components.add(proxy);
        }
    }

    public void onRemove(Object proxy)
    {
        synchronized (components)
        {
            final boolean wasRemoved = components.remove(proxy);
            if (wasRemoved)
            {
                ((Disposable) proxy).dispose();
            }
        }
    }

    public void start() throws UMOException
    {
        synchronized (components)
        {
            for (Iterator i = components.iterator(); i.hasNext();)
            {
                ((Startable) i.next()).start();
            }
        }
    }

    public void stop() throws UMOException
    {
        synchronized (components)
        {
            for (Iterator i = components.iterator(); i.hasNext();)
            {
                ((Stoppable) i.next()).stop();
            }
        }
    }

}

/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.mule.commonspool;

import org.mule.tck.model.AbstractPoolTestCase;

// TODO Update after MULE-2233
public class CommonsPoolTestCase extends AbstractPoolTestCase
{
//    public ObjectPool createPool(MuleDescriptor descriptor, byte action) throws InitialisationException
//    {
//        GenericObjectPool.Config config = new GenericObjectPool.Config();
//        config.maxActive = DEFAULT_POOL_SIZE;
//        config.maxWait = DEFAULT_WAIT;
//
//        if (action == FAIL_WHEN_EXHAUSTED)
//        {
//            config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_FAIL;
//        }
//        else if (action == GROW_WHEN_EXHAUSTED)
//        {
//            config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_GROW;
//        }
//        else if (action == BLOCK_WHEN_EXHAUSTED)
//        {
//            config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
//        }
//        else
//        {
//            fail("Action type for pool not recognised. Type is: " + action);
//        }
//        return new CommonsPoolProxyPool(descriptor, new TestSedaModel(), config);
//    }
//
//    public UMOPoolFactory getPoolFactory()
//    {
//        return new CommonsPoolFactory();
//    }
}

/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.udp.functional;

import org.mule.api.MuleContext;
import org.mule.transport.udp.UdpConnector;

import org.apache.commons.pool.impl.GenericKeyedObjectPool;

/**
 * Allows access to the dispatchers pool so we can ensure they're disposed
 */
public class CustomUdpConnector extends UdpConnector
{
    public CustomUdpConnector(MuleContext context)
    {
        super(context);
    }
    
    public GenericKeyedObjectPool getDispatchers() 
    {
        return dispatchers;
    }
}



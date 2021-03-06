/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.management.mbeans;

/**
 * The EndpointServiceMBean allows you to check the confiugration of an endpoint
 * and conect/disconnect endpoints manually.
 * 
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public interface EndpointServiceMBean
{

    String getAddress();

    String getName();

    boolean isConnected();

    void connect() throws Exception;

    void disconnect() throws Exception;

    String getType();

    boolean isSynchronous();

    String getComponentName();
}

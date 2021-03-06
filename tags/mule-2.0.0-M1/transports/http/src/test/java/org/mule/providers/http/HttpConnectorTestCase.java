/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.http;

import org.mule.impl.MuleDescriptor;
import org.mule.impl.endpoint.MuleEndpoint;
import org.mule.impl.endpoint.MuleEndpointURI;
import org.mule.providers.tcp.TcpConnector;
import org.mule.tck.providers.AbstractConnectorTestCase;
import org.mule.tck.testmodels.fruit.Orange;
import org.mule.umo.UMOComponent;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.umo.provider.UMOConnector;

public class HttpConnectorTestCase extends AbstractConnectorTestCase
{
    public UMOConnector getConnector() throws Exception
    {
        HttpConnector c = new HttpConnector();
        c.setName("HttpConnector");
        return c;
    }

    public String getTestEndpointURI()
    {
        return "http://localhost:60127";
    }

    public Object getValidMessage() throws Exception
    {
        return "Hello".getBytes();
    }

    public void testValidListener() throws Exception
    {
        MuleDescriptor d = getTestDescriptor("orange", Orange.class.getName());
        UMOComponent component = getTestComponent(d);
        UMOEndpoint endpoint = new MuleEndpoint(getTestEndpointURI(), true);

        try
        {
            endpoint.setEndpointURI(null);
            fail("cannot register with empty endpointUri");
        }
        catch (Exception e)
        {
            /* expected */
        }

        endpoint.setEndpointURI(new MuleEndpointURI(getTestEndpointURI()));
        connector.registerListener(component, endpoint);
    }

    public void testProperties() throws Exception
    {
        HttpConnector c = (HttpConnector)getConnector();

        c.setSendBufferSize(1024);
        assertEquals(1024, c.getSendBufferSize());
        c.setSendBufferSize(0);
        assertEquals(TcpConnector.DEFAULT_BUFFER_SIZE, c.getSendBufferSize());

        c.dispose();

        // all kinds of timeouts are now being tested in TcpConnectorTestCase
    }
}

/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.cxf;

import org.mule.DefaultMuleMessage;
import org.mule.api.transport.Connector;
import org.mule.transport.AbstractConnectorTestCase;

public class CxfConnectorTestCase extends AbstractConnectorTestCase
{
    @Override
    public String getTestEndpointURI()
    {
        return "cxf:http://localhost:38009/cxf";
    }

    @Override
    public Object getValidMessage() throws Exception
    {
        return new DefaultMuleMessage("", muleContext);
    }

    @Override
    public Connector createConnector() throws Exception
    {
        CxfConnector c = new CxfConnector(muleContext);
        c.setName("cxfConnector");
        return c;
    }
}

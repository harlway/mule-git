/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.providers.email;

import org.mule.providers.email.ImapsConnector;
import org.mule.umo.provider.UMOConnector;

/**
 * Simple tests for pulling from an IMAP server.
 */
public class ImapsConnectorTestCase extends AbstractReceivingMailConnectorTestCase
{

    public UMOConnector getConnector() throws Exception
    {
        ImapsConnector connector = new ImapsConnector();
        connector.setName("ImapsConnector");
        connector.setCheckFrequency(POLL_PERIOD_MS);
        connector.setTrustStorePassword("password");
        connector.setTrustStore("greenmail-truststore");
        connector.initialise();
        return connector;
    }

    public String getTestEndpointURI()
    {
        return getImapsTestEndpointURI();
    }

}

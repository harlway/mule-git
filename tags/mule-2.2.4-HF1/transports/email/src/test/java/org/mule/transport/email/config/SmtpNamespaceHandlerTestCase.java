/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.email.config;

import org.mule.api.MuleException;
import org.mule.transport.email.SmtpConnector;
import org.mule.transport.email.SmtpsConnector;

import java.util.Properties;

public class SmtpNamespaceHandlerTestCase extends AbstractEmailNamespaceHandlerTestCase
{
    protected String getConfigResources()
    {
        return "smtp-namespace-config.xml";
    }

    public void testConfig() throws Exception
    {
        SmtpConnector c = (SmtpConnector)muleContext.getRegistry().lookupConnector("smtpConnector");
        assertNotNull(c);

        assertEquals("abc@example.com", c.getBccAddresses());
        assertEquals("bcd@example.com", c.getCcAddresses());
        assertEquals("foo/bar", c.getContentType());
        Properties headers = c.getCustomHeaders();
        assertEquals(2, headers.size());
        assertEquals("bar", headers.getProperty("foo"));
        assertEquals("boz", headers.getProperty("baz"));
        assertEquals("cde@example.com", c.getFromAddress());
        assertEquals("def@example.com", c.getReplyToAddresses());
        assertEquals("subject", c.getSubject());

        // authenticator?

        assertTrue(c.isConnected());
        assertTrue(c.isStarted());
    }

    public void testSecureConfig() throws Exception
    {
        SmtpsConnector c = (SmtpsConnector)muleContext.getRegistry().lookupConnector("smtpsConnector");
        assertNotNull(c);

        assertEquals("abc@example.com", c.getBccAddresses());
        assertEquals("bcd@example.com", c.getCcAddresses());
        assertEquals("foo/bar", c.getContentType());
        Properties headers = c.getCustomHeaders();
        assertEquals(2, headers.size());
        assertEquals("bar", headers.getProperty("foo"));
        assertEquals("boz", headers.getProperty("baz"));
        assertEquals("cde@example.com", c.getFromAddress());
        assertEquals("def@example.com", c.getReplyToAddresses());
        assertEquals("subject", c.getSubject());

        // authenticator?

        //The full path gets resolved, we're just checkng that the property got set
        assertTrue(c.getClientKeyStore().endsWith("/greenmail-truststore"));
        assertEquals("password", c.getClientKeyStorePassword());
        //The full path gets resolved, we're just checkng that the property got set
        assertTrue(c.getTrustStore().endsWith("/greenmail-truststore"));
        assertEquals("password", c.getTrustStorePassword());

        assertTrue(c.isConnected());
        assertTrue(c.isStarted());
    }

    public void testEndpoint() throws MuleException
    {
        testEndpoint("global1", SmtpConnector.SMTP);
        testEndpoint("global2", SmtpConnector.SMTP);
        testEndpoint("global1s", SmtpsConnector.SMTPS);
        testEndpoint("global2s", SmtpsConnector.SMTPS);
    }
}
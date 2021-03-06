/*
 * $Id: 
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.extras.wssecurity.testcases;

import org.mule.extras.client.MuleClient;
import org.mule.tck.FunctionalTestCase;
import org.mule.umo.UMOMessage;

import java.util.Properties;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;

public class XfireSamlTestCase extends FunctionalTestCase
{
    // The test cases have been suppressed because for JDK 1.4, the Xerces parser
    // must be in an endorsed file for SAML to work. Everything works fine on JDK 1.5

    public void testBogus() throws Exception
    {
        // no test
    }

    public void _testGoodUnsignedSamlTokenAuthentication() throws Exception
    {
        MuleClient client = new MuleClient();
        Properties props = new Properties();

        // Action to perform : saml token
        props.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.SAML_TOKEN_UNSIGNED);
        // saml configuration file
        props.setProperty(WSHandlerConstants.SAML_PROP_FILE, "saml.properties");
        // Password type : text or digest
        props.setProperty(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
        // User name to send
        props.setProperty(WSHandlerConstants.USER, "mulealias");
        // Callback used to retrive password for given user.
        props.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS,
            "org.mule.extras.wssecurity.callbackhandlers.MuleWsSecurityCallbackHandler");

        UMOMessage m = client.send("xfire:http://localhost:8282/MySecuredUMO?method=echo", "Test",
            props);
        assertNotNull(m);
        assertTrue(m.getPayload() instanceof String);
        assertTrue(((String)m.getPayload()).equals("Test"));
    }

    public void _testBadUnsignedSamlTokenAuthentication() throws Exception
    {
        MuleClient client = new MuleClient();
        Properties props = new Properties();

        // Action to perform : user token
        props.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        // Password type : text or digest
        props.setProperty(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
        // User name to send
        props.setProperty(WSHandlerConstants.USER, "baduser");
        // Callback used to retrive password for given user.
        props.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS,
            "org.mule.extras.wssecurity.callbackhandlers.MuleWsSecurityCallbackHandler");

        UMOMessage m = null;
        try
        {
            m = client.send("xfire:http://localhost:8282/MySecuredUMO?method=echo", "Test", props);
        }
        catch (Exception e)
        {
            assertNotNull(e);
        }
        assertNull(m);
    }

    public void _testGoodSignedSamlTokenAuthentication() throws Exception
    {
        MuleClient client = new MuleClient();
        Properties props = new Properties();

        // Action to perform : saml token
        props.setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.SAML_TOKEN_SIGNED);
        // saml configuration file
        props.setProperty(WSHandlerConstants.SAML_PROP_FILE, "saml.properties");
        // Password type : text or digest
        props.setProperty(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
        // User name to send
        props.setProperty(WSHandlerConstants.USER, "mulealias");
        // Callback used to retrive password for given user.
        props.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS,
            "org.mule.extras.wssecurity.callbackhandlers.MuleWsSecurityCallbackHandler");
        // Configuration for accessing private key in keystore
        props.setProperty(WSHandlerConstants.SIG_PROP_FILE, "out-signed-security.properties");
        // "IssuerSerial" is not supported
        props.setProperty(WSHandlerConstants.SIG_KEY_ID, "DirectReference");

        UMOMessage m = client.send("xfire:http://localhost:8282/MySecuredUMO?method=echo", "Test",
            props);
        assertNotNull(m);
        assertTrue(m.getPayload() instanceof String);
        assertTrue(((String)m.getPayload()).equals("Test"));
    }

    protected String getConfigResources()
    {
        return "wssecurity-mule-config-for-inbound.xml";
    }
}
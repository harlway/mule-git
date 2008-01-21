/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.extras.jaas;

import org.mule.config.MuleProperties;
import org.mule.extras.client.MuleClient;
import org.mule.impl.security.MuleCredentials;
import org.mule.tck.FunctionalTestCase;
import org.mule.umo.UMOEncryptionStrategy;
import org.mule.umo.UMOExceptionPayload;
import org.mule.umo.UMOMessage;

import java.util.HashMap;
import java.util.Map;

public class JaasAutenticationWithJaasConfigFileTestCase extends FunctionalTestCase
{

    public JaasAutenticationWithJaasConfigFileTestCase()
    {
        super();
        this.setDisposeManagerPerSuite(true);
    }

    public void testCaseGoodAuthentication() throws Exception
    {
        MuleClient client = new MuleClient();

        Map props = new HashMap();
        UMOEncryptionStrategy strategy = muleContext
            .getSecurityManager()
            .getEncryptionStrategy("PBE");
        String header = MuleCredentials.createHeader("Marie.Rizzo", "dragon", "PBE", strategy);
        props.put(MuleProperties.MULE_USER_PROPERTY, header);
        UMOMessage m = client.send("vm://test", "Test", props);

        assertNotNull(m);
        assertTrue(m.getPayload() instanceof String);
        assertTrue(m.getPayloadAsString().equals("Test Received"));
    }

    public void testCaseDifferentGoodAuthentication() throws Exception
    {
        MuleClient client = new MuleClient();

        Map props = new HashMap();
        UMOEncryptionStrategy strategy = muleContext
            .getSecurityManager()
            .getEncryptionStrategy("PBE");
        String header = MuleCredentials.createHeader("anon", "anon", "PBE", strategy);
        props.put(MuleProperties.MULE_USER_PROPERTY, header);
        UMOMessage m = client.send("vm://test", "Test", props);

        assertNotNull(m);
        assertTrue(m.getPayload() instanceof String);
        assertTrue(m.getPayloadAsString().equals("Test Received"));
    }

    public void testCaseWrongCombinationOfCorrectUsernameAndPassword() throws Exception
    {
        MuleClient client = new MuleClient();

        Map props = new HashMap();
        UMOEncryptionStrategy strategy = muleContext
            .getSecurityManager()
            .getEncryptionStrategy("PBE");
        String header = MuleCredentials.createHeader("Marie.Rizzo", "anon", "PBE", strategy);
        props.put(MuleProperties.MULE_USER_PROPERTY, header);
        UMOMessage m = client.send("vm://test", "Test", props);

        assertNotNull(m);
        assertTrue(m.getPayload() instanceof String);
        assertFalse(m.getPayloadAsString().equals("Test Received"));
        
        //assert exception
        UMOExceptionPayload exceptionPayload = m.getExceptionPayload();
        assertNotNull(exceptionPayload);
        assertEquals("Authentication failed for principal Marie.Rizzo. Message payload is of type: String", exceptionPayload.getMessage());
    }

    public void testCaseBadUserName() throws Exception
    {
        MuleClient client = new MuleClient();
        Map props = new HashMap();
        UMOEncryptionStrategy strategy = muleContext
            .getSecurityManager()
            .getEncryptionStrategy("PBE");
        String header = MuleCredentials.createHeader("Evil", "dragon", "PBE", strategy);
        props.put(MuleProperties.MULE_USER_PROPERTY, header);
        UMOMessage m = client.send("vm://test", "Test", props);

        assertNotNull(m);
        assertTrue(m.getPayload() instanceof String);
        assertFalse(m.getPayloadAsString().equals("Test Received"));
        
        //assert exception
        UMOExceptionPayload exceptionPayload = m.getExceptionPayload();
        assertNotNull(exceptionPayload);
        assertEquals("Authentication failed for principal Evil. Message payload is of type: String", exceptionPayload.getMessage());
        
    }

    public void testCaseBadPassword() throws Exception
    {
        MuleClient client = new MuleClient();
        Map props = new HashMap();
        UMOEncryptionStrategy strategy = muleContext
            .getSecurityManager()
            .getEncryptionStrategy("PBE");
        String header = MuleCredentials.createHeader("Marie.Rizzo", "evil", "PBE", strategy);
        props.put(MuleProperties.MULE_USER_PROPERTY, header);
        UMOMessage m = client.send("vm://test", "Test", props);

        assertNotNull(m);
        assertTrue(m.getPayload() instanceof String);
        assertFalse(m.getPayloadAsString().equals("Test Received"));
  
        //assert exception
        UMOExceptionPayload exceptionPayload = m.getExceptionPayload();
        assertNotNull(exceptionPayload);
        assertEquals("Authentication failed for principal Marie.Rizzo. Message payload is of type: String", exceptionPayload.getMessage());
        
        
    }

    protected String getConfigResources()
    {
        return "mule-conf-for-jaas-conf-file.xml";
    }
}

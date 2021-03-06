/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.vm.issues;

import org.mule.tck.FunctionalTestCase;
import org.mule.extras.client.MuleClient;
import org.mule.umo.UMOException;
import org.mule.umo.UMOMessage;

public class EndpointTransformerMule2131TestCase extends FunctionalTestCase
{

    public static final long TIMEOUT = 1000L;
    public static final String MESSAGE = "a message";

    protected String getConfigResources()
    {
        return "endpoint-transformer-mule-2131-test.xml";
    }

    protected MuleClient send() throws UMOException
    {
        MuleClient client = new MuleClient();
        client.dispatch("in", MESSAGE, null);
        return client;
    }

    public void testDirect() throws Exception
    {
        String response = receive(send(), "direct");
        assertEquals(MESSAGE, response);
    }

    public void testGlobalNameGlobalTransformer() throws Exception
    {
        doTestTransformed("global-name-global-transformer");
    }

    public void testGlobalNameLocalTransformer() throws Exception
    {
        doTestTransformed("global-name-local-transformer");
    }

    public void testNoNameLocalTransformer() throws Exception
    {
        doTestTransformed("vm://no-name-local-transformer?connector=queue");
    }

    // not possible before 2.0?
//    public void testLocalNameLocalTransformer() throws Exception
//    {
//        doTestTransformed("local-name-local-transformer");
//    }

    protected void doTestTransformed(String endpoint) throws Exception
    {
        String response = receive(send(), endpoint);
        assertEquals(MESSAGE + StringAppendTransformer.DEFAULT_TEXT, response);
    }

    protected String receive(MuleClient client, String endpoint) throws Exception
    {
        UMOMessage message = client.receive(endpoint, TIMEOUT);
        assertNotNull(message);
        assertNotNull(message.getPayloadAsString());
        return message.getPayloadAsString();
    }

}
/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.client;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;

public class MuleClientStandaloneTestCase extends AbstractMuleClientTestCase
{

    protected MuleContext createMuleContext() throws Exception
    {
        return null;
    }

    public void testCreateMuleClient() throws MuleException
    {
        MuleClient muleClient = new MuleClient();
        assertNotSame(muleContext, muleClient.getMuleContext());
        assertTrue(muleClient.getMuleContext().isInitialised());
        assertTrue(muleClient.getMuleContext().isStarted());
        muleClient.dispatch("test://test", "message", null);
        muleClient.send("test://test", "message", null);
        muleClient.dispose();
        assertFalse(muleClient.getMuleContext().isInitialised());
        assertFalse(muleClient.getMuleContext().isStarted());
    }

}

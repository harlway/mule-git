/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.providers.jms.functional;

/**
 * Send and recieve JmsMessage without any tx
 */
public class JmsSingleTransactionNoneTestCase extends AbstractJmsFunctionalTestCase
{
    protected String getConfigResources()
    {
        return "providers/activemq/jms-single-tx-NONE.xml";
    }

    public void testNoneTx() throws Exception
    {
        send(scenarioNoTx);
        receive(scenarioNoTx);
        receive(scenarioNotReceive);
    }
}

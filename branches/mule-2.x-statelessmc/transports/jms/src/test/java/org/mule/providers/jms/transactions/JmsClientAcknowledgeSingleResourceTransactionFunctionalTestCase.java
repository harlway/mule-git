/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.jms.transactions;


public class JmsClientAcknowledgeSingleResourceTransactionFunctionalTestCase extends JmsClientAcknowledgeTransactionFunctionalTestCase
{
    protected String getConfigResources()
    {
        return "activemq-client-ack-single-resource.xml," + super.getBaseConfigResources();
    }
}

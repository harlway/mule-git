/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.transaction;

import org.mule.api.transaction.Transaction;
import org.mule.transaction.TransactionCoordination;

import java.util.Map;

/**
 * Simple service that receives messages from jdbc or jms and just forward the
 * interesting part.
 */
public class XABridgeComponent
{

    public static boolean mayRollback = false;

    /**
     * If <code>mayRollback</code> has been set to true, the service will mark
     * the current transaction as rollback only on a 30 percent basis.
     * 
     * @throws Exception
     */
    protected void mayRollback() throws Exception
    {
        if (mayRollback)
        {
            Transaction tx = TransactionCoordination.getInstance().getTransaction();
            if (tx != null)
            {
                if (Math.random() < 0.3)
                {
                    System.err.println("Marking transaction for rollback");
                    tx.setRollbackOnly();
                }
            }
        }
    }

    /**
     * Receive the jdbc message and forward the <code>data</code> part. May mark
     * the current transaction as rollback only.
     * 
     * @param msg
     * @return
     * @throws Exception
     */
    public Object onJdbcMessage(Map msg) throws Exception
    {
        mayRollback();
        return msg.get("data").toString();
    }

    /**
     * Receive the content of the jms message and forward it. May mark the current
     * transaction as rollback only.
     * 
     * @param msg
     * @return
     * @throws Exception
     */
    public Object onJmsMessage(String msg) throws Exception
    {
        mayRollback();
        return msg;
    }

}

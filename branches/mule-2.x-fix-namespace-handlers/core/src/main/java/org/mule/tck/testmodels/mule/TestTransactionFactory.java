/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.tck.testmodels.mule;

import org.mule.umo.TransactionException;
import org.mule.umo.UMOManagementContext;
import org.mule.umo.UMOTransaction;
import org.mule.umo.UMOTransactionFactory;

import com.mockobjects.dynamic.Mock;

/**
 * <code>TestTransactionFactory</code> TODO
 * 
 */

public class TestTransactionFactory implements UMOTransactionFactory
{
    public UMOTransaction beginTransaction(UMOManagementContext managementContext) throws TransactionException
    {
        return (UMOTransaction) new Mock(UMOTransaction.class).proxy();
    }

    public boolean isTransacted()
    {
        return true;
    }
}

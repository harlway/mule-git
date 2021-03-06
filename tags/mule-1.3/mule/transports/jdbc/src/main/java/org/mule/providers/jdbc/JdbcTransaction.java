/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.providers.jdbc;

import org.mule.config.i18n.Message;
import org.mule.config.i18n.Messages;
import org.mule.transaction.AbstractSingleResourceTransaction;
import org.mule.transaction.IllegalTransactionStateException;
import org.mule.transaction.TransactionRollbackException;
import org.mule.umo.TransactionException;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Guillaume Nodet
 * @version $Revision$
 */
public class JdbcTransaction extends AbstractSingleResourceTransaction
{

    public JdbcTransaction()
    {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.umo.UMOTransaction#bindResource(java.lang.Object,
     *      java.lang.Object)
     */
    public void bindResource(Object key, Object resource) throws TransactionException
    {
        if (!(key instanceof DataSource) || !(resource instanceof Connection)) {
            throw new IllegalTransactionStateException(new Message(Messages.TX_CAN_ONLY_BIND_TO_X_TYPE_RESOURCES,
                                                                   "javax.sql.DataSource/java.sql.Connection"));
        }
        Connection con = (Connection) resource;
        try {
            if (con.getAutoCommit()) {
                con.setAutoCommit(false);
            }
        } catch (SQLException e) {
            throw new TransactionException(new Message(Messages.TX_SET_AUTO_COMMIT_FAILED), e);
        }
        super.bindResource(key, resource);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.transaction.AbstractSingleResourceTransaction#doBegin()
     */
    protected void doBegin() throws TransactionException
    {
        // Do nothing
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.transaction.AbstractSingleResourceTransaction#doCommit()
     */
    protected void doCommit() throws TransactionException
    {
        try {
            ((Connection) resource).commit();
            ((Connection) resource).close();
        } catch (SQLException e) {
            throw new TransactionException(new Message(Messages.TX_COMMIT_FAILED), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.transaction.AbstractSingleResourceTransaction#doRollback()
     */
    protected void doRollback() throws TransactionException
    {
        try {
            ((Connection) resource).rollback();
            ((Connection) resource).close();
        } catch (SQLException e) {
            throw new TransactionRollbackException(new Message(Messages.TX_ROLLBACK_FAILED), e);
        }
    }

}

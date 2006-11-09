/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transaction;

import org.mule.config.i18n.Message;

/**
 * <p>
 * <code>IllegalTransactionStateException</code> TODO (document class)
 * 
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class IllegalTransactionStateException extends TransactionStatusException
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = -1062247038945694389L;

    /**
     * @param message the exception message
     */
    public IllegalTransactionStateException(Message message)
    {
        super(message);
    }

    /**
     * @param message the exception message
     * @param cause the exception that cause this exception to be thrown
     */
    public IllegalTransactionStateException(Message message, Throwable cause)
    {
        super(message, cause);
    }
}

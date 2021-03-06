/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.persistence;

import org.mule.MuleException;

/**
 * <code>PersistenceException</code> is the exception thrown by
 * the PersistenceStore and/or Manager.
 */
public class PersistenceException extends MuleException
{
    public PersistenceException()
    {
        super("");
    }
}


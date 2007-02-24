/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * <code>NullPayload</code> represents a null event payload
 */
// @Immutable
public class NullPayload implements Serializable
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 3530905899811505080L;

    private static class NullPayloadHolder
    {
        private static final NullPayload instance = new NullPayload();
    }

    public static NullPayload getInstance()
    {
        return NullPayloadHolder.instance;
    }

    private NullPayload()
    {
        super();
    }

    private Object readResolve() throws ObjectStreamException
    {
        return NullPayloadHolder.instance;
    }

    // @Override
    public boolean equals(Object obj)
    {
        return obj instanceof NullPayload;
    }

    // @Override
    public String toString()
    {
        return "{NullPayload}";
    }

}

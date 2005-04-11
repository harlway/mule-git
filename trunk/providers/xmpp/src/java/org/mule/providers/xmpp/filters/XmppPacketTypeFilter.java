/*
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) Cubis Limited. All rights reserved.
 * http://www.cubis.co.uk
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.mule.providers.xmpp.filters;

import org.jivesoftware.smack.filter.PacketFilter;

/**
 * <code>XmppPacketTypeFilter</code> is an Xmpp PacketTypeFilter
 * adapter.
 *
 * @author <a href="mailto:ross.mason@cubis.co.uk">Ross Mason</a>
 * @version $Revision$
 */
public class XmppPacketTypeFilter extends AbstractXmppFilter
{
    private Class expectedType;

    public XmppPacketTypeFilter() {
    }

    public XmppPacketTypeFilter(Class expectedType) {
        setExpectedType(expectedType);
    }

    public Class getExpectedType() {
        return expectedType;
    }

    public void setExpectedType(Class expectedType) {
        this.expectedType = expectedType;
    }

    protected PacketFilter createFilter() {
        return new org.jivesoftware.smack.filter.PacketTypeFilter(expectedType);
    }
}

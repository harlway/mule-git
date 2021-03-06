/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.api.security;

import org.mule.api.MuleException;
import org.mule.config.i18n.CoreMessages;

/**
 * <code>SecurityProviderNotFoundException</code> is thrown by the
 * SecurityManager when an authentication request is made but no suitable security
 * provider can be found to process the authentication
 */
public class SecurityProviderNotFoundException extends MuleException
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 124630897095610595L;

    public SecurityProviderNotFoundException(String providerName)
    {
        super(CoreMessages.authNoSecurityProvider(providerName));
    }

    public SecurityProviderNotFoundException(String providerName, Throwable cause)
    {
        super(CoreMessages.authNoSecurityProvider(providerName), cause);
    }
}

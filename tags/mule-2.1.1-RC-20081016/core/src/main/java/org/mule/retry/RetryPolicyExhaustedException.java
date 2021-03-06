/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.retry;

import org.mule.api.lifecycle.FatalException;
import org.mule.config.i18n.Message;

/** TODO document */
public class RetryPolicyExhaustedException extends FatalException
{
    /** Serial version */
    private static final long serialVersionUID = 3300563235465630595L;

    public RetryPolicyExhaustedException(Message message, Object component)
    {
        super(message, component);
    }

    public RetryPolicyExhaustedException(Message message, Throwable cause, Object component)
    {
        super(message, cause, component);
    }

    public RetryPolicyExhaustedException(Throwable cause, Object component)
    {
        super(cause, component);
    }
}

/*
 * $Id$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) SymphonySoft Limited. All rights reserved.
 * http://www.symphonysoft.com
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.mule.impl;

import org.mule.umo.UMOEvent;
import org.mule.umo.UMOEventContext;
import org.mule.umo.UMOExceptionPayload;
import org.mule.umo.UMOMessage;

/**
 * <code>RequestContext</code> is a thread context where components can get
 * the current event or set response properties that will be sent on the
 * outgoing message.
 *
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class RequestContext
{
    private static ThreadLocal currentEvent = new ThreadLocal();

    public static UMOEventContext getEventContext()
    {
        UMOEvent event = getEvent();
        if (event != null) {
            return new MuleEventContext(event);
        }
        else {
            return null;
        }
    }

    public static UMOEvent getEvent()
    {
        return (UMOEvent) currentEvent.get();
    }

    public static void setEvent(UMOEvent event)
    {
        currentEvent.set(event);
    }

    /**
     * Sets a new message payload in the RequestContext but maintains all other
     * properties (session, endpoint, synchronous, etc.) from the previous event.
     *
     * @param message - current message payload
     */
    public static void rewriteEvent(UMOMessage message)
    {
        if (message != null) {
            UMOEvent event = getEvent();
            if (event != null) {
                event = new MuleEvent(message, event);
                setEvent(event);
            }
        }
    }

    /**
     * Resets the current request context (clears all information).
     */
    public static void clear()
    {
        setEvent(null);
    }

    public static void setExceptionPayload(UMOExceptionPayload exceptionPayload)
    {
        getEvent().getMessage().setExceptionPayload(exceptionPayload);
    }

    public static UMOExceptionPayload getExceptionPayload()
    {
        return getEvent().getMessage().getExceptionPayload();
    }

}

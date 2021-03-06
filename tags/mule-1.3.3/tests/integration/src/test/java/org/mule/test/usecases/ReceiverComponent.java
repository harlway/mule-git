/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.usecases;

import org.mule.umo.UMOEventContext;
import org.mule.umo.lifecycle.Callable;

/**
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class ReceiverComponent implements Callable
{
    public Object onCall(UMOEventContext eventContext) throws Exception
    {
        return "Received: " + eventContext.getMessageAsString();
    }
}

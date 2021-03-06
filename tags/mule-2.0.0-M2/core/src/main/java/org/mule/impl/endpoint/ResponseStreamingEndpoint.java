/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.impl.endpoint;

public class ResponseStreamingEndpoint extends ResponseEndpoint
{

    private static final long serialVersionUID = -61001693627526734L;
    
    public boolean isStreaming()
    {
        return true;
    }

}
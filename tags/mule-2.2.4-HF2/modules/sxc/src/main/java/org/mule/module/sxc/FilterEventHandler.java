/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.sxc;

import org.mule.api.MuleMessage;
import org.mule.api.routing.RoutingException;
import org.mule.api.transport.PropertyScope;

import com.envoisolutions.sxc.xpath.XPathEvent;
import com.envoisolutions.sxc.xpath.XPathEventHandler;

import javax.xml.stream.XMLStreamException;

public class FilterEventHandler extends XPathEventHandler
{
    SxcFilteringOutboundRouter router;
    SxcFilter filter;
    
    public FilterEventHandler(SxcFilteringOutboundRouter router, SxcFilter filter)
    {
        super();
        this.router = router;
        this.filter = filter;
    }
    
    @Override
    public void onMatch(XPathEvent event) throws XMLStreamException
    {
        try 
        {
            MuleMessage msg = SxcFilteringOutboundRouter.getCurrentMessage();
            msg.setProperty(filter.toString(), true, PropertyScope.INVOCATION);
            
            if (router.testMatch(msg))
            {
                throw new StopProcessingException();
            }
        }
        catch (UndefinedMatchException e) 
        {
        }
        catch (RoutingException e)
        {
            // This shouldn't happen
            throw new RuntimeException(e);
        }
    }

}



/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.http.jetty;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.mule.config.i18n.Message;
import org.mule.providers.AbstractMessageReceiver;
import org.mule.providers.http.servlet.MuleReceiverServlet;
import org.mule.umo.endpoint.EndpointException;

/**
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */

public class JettyReceiverServlet extends MuleReceiverServlet
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 238326861089137293L;

    private AbstractMessageReceiver receiver;

    protected void doInit(ServletConfig servletConfig) throws ServletException
    {
        receiver = (AbstractMessageReceiver)servletConfig.getServletContext().getAttribute("messageReceiver");
        if (receiver == null)
        {
            throw new ServletException(new Message("http", 7).toString());
        }
    }

    protected AbstractMessageReceiver getReceiverForURI(HttpServletRequest httpServletRequest)
        throws EndpointException
    {
        return receiver;
    }
}

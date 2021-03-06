/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.routing.outbound;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.MuleSession;
import org.mule.api.endpoint.EndpointNotFoundException;
import org.mule.api.endpoint.MalformedEndpointException;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.expression.ExpressionRuntimeException;
import org.mule.api.routing.CouldNotRouteOutboundMessageException;
import org.mule.api.routing.RoutingException;
import org.mule.config.i18n.CoreMessages;
import org.mule.expression.ExpressionConfig;
import org.mule.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <code>EndpointSelector</code> selects the outgoing endpoint based on a
 * an expression evaluator  ("header:endpoint" by default).  It will first try to match the
 * endpoint by name and then by address.
 * The endpoints to use can be set on the router itself or be global endpoint definitions.
 * <pre>
 * <p/>
 * &lt;outbound&gt;
 *      &lt;endpoint-selector-router evaluator="xpath" expression="/MSG/HEADER/NEXT-ADDRESS"&gt;
 *          &lt;endpoint name="dest1" address="jms://queue1" /&gt;
 *          &lt;endpoint name="dest2" address="jms://queue2" /&gt;
 *          &lt;endpoint name="dest3" address="jms://queue3" /&gt;
 *      &lt;/endpoint-selector-router&gt;
 * &lt;/outbound&gt;
 * <p/>
 * </pre>
 */
public class EndpointSelector extends FilteringOutboundRouter
{
    public static final String DEFAULT_SELECTOR_EVALUATOR = "header";
    public static final String DEFAULT_SELECTOR_EXPRESSION = "endpoint";

    private String defaultEndpointName;

    private ExpressionConfig expressionConfig = new ExpressionConfig(DEFAULT_SELECTOR_EXPRESSION, DEFAULT_SELECTOR_EVALUATOR, null);

    public MuleMessage route(MuleMessage message, MuleSession session)
            throws RoutingException
    {
        List<Object> endpoints;
        String endpointName;

        String prop = expressionConfig.getFullExpression(expressionManager);
        if (!muleContext.getExpressionManager().isValidExpression(prop))
        {
            throw new CouldNotRouteOutboundMessageException(
                    CoreMessages.expressionInvalidForProperty("expression", prop), message, null);
        }

        Object property = null;
        try
        {
            property = muleContext.getExpressionManager().evaluate(prop, message);
        }
        catch (ExpressionRuntimeException e)
        {
            logger.error(e.getMessage());
        }
        
        if (property == null && getDefaultEndpointName() == null)
        {
            throw new CouldNotRouteOutboundMessageException(
                    CoreMessages.expressionResultWasNull(expressionConfig.getFullExpression(expressionManager)), message, null);
        }
        else if(property==null)
        {
            logger.info("Expression: " + prop + " returned null, using default endpoint: " + getDefaultEndpointName());
            property = getDefaultEndpointName();
        }

        if (property instanceof String)
        {
            endpoints = new ArrayList<Object>(1);
            endpoints.add(property);
        }
        else if (property instanceof List)
        {
            endpoints = (List<Object>) property;
        }
        else
        {
            throw new CouldNotRouteOutboundMessageException(CoreMessages.propertyIsNotSupportedType(
                    expressionConfig.getFullExpression(expressionManager), new Class[]{String.class, List.class}, property.getClass()), message, null);
        }

        List<MuleMessage> results = new ArrayList<MuleMessage>(endpoints.size());

        for (Iterator<Object> iterator = endpoints.iterator(); iterator.hasNext();)
        {
            endpointName = iterator.next().toString();

            if (StringUtils.isEmpty(endpointName))
            {
                throw new CouldNotRouteOutboundMessageException(
                        CoreMessages.objectIsNull("Endpoint Name: " + expressionConfig.getFullExpression(expressionManager)), message, null);
            }
            OutboundEndpoint ep = null;
            try
            {
                ep = lookupEndpoint(endpointName);
                if (ep == null)
                {
                    throw new CouldNotRouteOutboundMessageException(CoreMessages.objectNotFound("Endpoint",
                            endpointName), message, ep);
                }
                if (ep.isSynchronous())
                {
                    results.add(send(session, message, ep));
                }
                else
                {
                    dispatch(session, message, ep);
                }
            }
            catch (MuleException e)
            {
                throw new CouldNotRouteOutboundMessageException(message, ep, e);
            }
        }
        return resultsHandler.aggregateResults(results, message);
    }

    protected OutboundEndpoint lookupEndpoint(String endpointName) throws MuleException
    {
        OutboundEndpoint ep;
        Iterator iterator = endpoints.iterator();
        while (iterator.hasNext())
        {
            ep = (OutboundEndpoint) iterator.next();
            // Endpoint identifier (deprecated)
            if (endpointName.equals(ep.getName()))
            {
                return ep;
            }
            // Global endpoint
            else if (endpointName.equals(ep.getName()))
            {
                return ep;
            }
            else if (endpointName.equals(ep.getEndpointURI().getUri().toString()))
            {
                return ep;
            }
        }
        try
        {
            return getMuleContext().getRegistry().lookupEndpointFactory().getOutboundEndpoint(endpointName);
        }
        catch (MalformedEndpointException e)
        {
            throw new EndpointNotFoundException(CoreMessages.endpointNotFound(endpointName), e);
        }
    }



    public String getExpression()
    {
        return expressionConfig.getExpression();
    }

    public void setExpression(String expression)
    {
        expressionConfig.setExpression(expression);
    }

    public String getCustomEvaluator()
    {
        return expressionConfig.getCustomEvaluator();
    }

    public void setCustomEvaluator(String customEvaluator)
    {
        expressionConfig.setCustomEvaluator(customEvaluator);
    }

    public String getEvaluator()
    {
        return expressionConfig.getEvaluator();
    }

    public void setEvaluator(String evaluator)
    {
        expressionConfig.setEvaluator(evaluator);
    }

    public String getDefaultEndpointName()
    {
        return defaultEndpointName;
    }

    public void setDefaultEndpointName(String defaultEndpointName)
    {
        this.defaultEndpointName = defaultEndpointName;
    }
}

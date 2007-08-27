/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.tck;

import org.mule.config.MuleProperties;
import org.mule.impl.DefaultExceptionStrategy;
import org.mule.impl.ManagementContext;
import org.mule.impl.MuleDescriptor;
import org.mule.impl.MuleEvent;
import org.mule.impl.MuleMessage;
import org.mule.impl.MuleSession;
import org.mule.impl.RequestContext;
import org.mule.impl.endpoint.MuleEndpoint;
import org.mule.impl.endpoint.MuleEndpointURI;
import org.mule.impl.model.seda.SedaComponent;
import org.mule.impl.model.seda.SedaModel;
import org.mule.providers.AbstractConnector;
import org.mule.providers.service.TransportFactory;
import org.mule.routing.inbound.InboundRouterCollection;
import org.mule.routing.outbound.OutboundPassThroughRouter;
import org.mule.routing.outbound.OutboundRouterCollection;
import org.mule.tck.testmodels.mule.TestAgent;
import org.mule.tck.testmodels.mule.TestCompressionTransformer;
import org.mule.tck.testmodels.mule.TestConnector;
import org.mule.umo.UMOComponent;
import org.mule.umo.UMODescriptor;
import org.mule.umo.UMOEvent;
import org.mule.umo.UMOEventContext;
import org.mule.umo.UMOException;
import org.mule.umo.UMOManagementContext;
import org.mule.umo.UMOSession;
import org.mule.umo.UMOTransaction;
import org.mule.umo.UMOTransactionFactory;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.umo.endpoint.UMOEndpointURI;
import org.mule.umo.endpoint.UMOImmutableEndpoint;
import org.mule.umo.provider.UMOConnector;
import org.mule.umo.provider.UMOMessageDispatcher;
import org.mule.umo.provider.UMOMessageDispatcherFactory;
import org.mule.umo.routing.UMOOutboundRouter;
import org.mule.umo.transformer.UMOTransformer;
import org.mule.util.ClassUtils;
import org.mule.util.object.SimpleObjectFactory;

import com.mockobjects.dynamic.Mock;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for creating test and Mock Mule objects
 */
public final class MuleTestUtils
{
    public static UMOEndpoint getTestEndpoint(String name, String type, UMOManagementContext context) throws Exception
    {
        Map props = new HashMap();
        props.put("name", name);
        props.put("type", type);
        props.put("endpointURI", new MuleEndpointURI("test://test"));
        props.put("connector", "testConnector");
        MuleEndpoint endpoint = new MuleEndpoint();
        // need to build endpoint this way to avoid depenency to any endpoint jars
        AbstractConnector connector = null;
        connector = (AbstractConnector)ClassUtils.loadClass("org.mule.tck.testmodels.mule.TestConnector",
            AbstractMuleTestCase.class).newInstance();

        connector.setName("testConnector");
        connector.setManagementContext(context);
        connector.initialise();
        endpoint.setConnector(connector);
        endpoint.setEndpointURI(new MuleEndpointURI("test://test"));
        endpoint.setName(name);
        endpoint.setType(type);
        endpoint.setManagementContext(context);
        endpoint.initialise();
        return endpoint;
    }
    
    public static UMOEndpoint getTestSchemeMetaInfoEndpoint(String name, String type, String protocol, UMOManagementContext context)
        throws Exception
    {
        UMOEndpoint endpoint = new MuleEndpoint();
        // need to build endpoint this way to avoid depenency to any endpoint jars
        AbstractConnector connector = null;
        connector = (AbstractConnector) ClassUtils.loadClass("org.mule.tck.testmodels.mule.TestConnector",
            AbstractMuleTestCase.class).newInstance();

        connector.setName("testConnector");
        connector.setManagementContext(context);
        connector.initialise();
        connector.registerSupportedProtocol(protocol);
        endpoint.setConnector(connector);
        endpoint.setEndpointURI(new MuleEndpointURI("test:" + protocol + "://test"));
        endpoint.setName(name);
        endpoint.setType(type);
        return endpoint;
    }

    public static UMOEvent getTestEvent(Object data, UMOManagementContext context) throws Exception
    {
        UMOComponent component = getTestComponent(getTestDescriptor("string", String.class.getName(), context));
        UMOSession session = getTestSession(component);
        return new MuleEvent(new MuleMessage(data, new HashMap()), getTestEndpoint("test1",
            UMOEndpoint.ENDPOINT_TYPE_SENDER, context), session, true);
    }

    public static UMOEventContext getTestEventContext(Object data, UMOManagementContext context) throws Exception
    {
        try
        {
            UMOEvent event = getTestEvent(data, context);
            RequestContext.setEvent(event);
            return RequestContext.getEventContext();
        }
        finally
        {
            RequestContext.setEvent(null);
        }
    }

    public static UMOTransformer getTestTransformer()
    {
        return new TestCompressionTransformer();
    }

    public static UMOEvent getTestEvent(Object data, MuleDescriptor descriptor, UMOManagementContext context) throws Exception
    {
        UMOComponent component = getTestComponent(descriptor);

        UMOSession session = getTestSession(component);

        UMOEndpoint endpoint = getTestEndpoint("test1", UMOEndpoint.ENDPOINT_TYPE_SENDER, context);

        return new MuleEvent(new MuleMessage(data, new HashMap()), endpoint, session, true);
    }

    public static UMOEvent getTestEvent(Object data, UMOImmutableEndpoint endpoint, UMOManagementContext context) throws Exception
    {
        UMOSession session = getTestSession(getTestComponent(getTestDescriptor("string", String.class
            .getName(), context)));
        return new MuleEvent(new MuleMessage(data, new HashMap()), endpoint, session, true);
    }

    public static UMOEvent getTestEvent(Object data, MuleDescriptor descriptor, UMOImmutableEndpoint endpoint)
        throws UMOException
    {
        UMOSession session = getTestSession(getTestComponent(descriptor));
        UMOEvent event = new MuleEvent(new MuleMessage(data, new HashMap()), endpoint, session, true);
        return event;
    }

    public static UMOSession getTestSession(UMOComponent component)
    {
        return new MuleSession(component);
    }

    public static UMOSession getTestSession()
    {
        return new MuleSession(null);
    }

    public static TestConnector getTestConnector()
    {
        TestConnector testConnector = new TestConnector();
        testConnector.setName("testConnector");
        return testConnector;
    }

    public static UMOComponent getTestComponent(MuleDescriptor descriptor)
    {
        return new SedaComponent(descriptor, new SedaModel());
    }

    public static MuleDescriptor getTestDescriptor(String name, String implementation, UMOManagementContext context) throws Exception
    {
        MuleDescriptor descriptor = new MuleDescriptor();
        descriptor.setExceptionListener(new DefaultExceptionStrategy());
        descriptor.setName(name);
        descriptor.setServiceFactory(new SimpleObjectFactory(implementation));
        UMOOutboundRouter router = new OutboundPassThroughRouter();
        router.addEndpoint(getTestEndpoint("test1", UMOEndpoint.ENDPOINT_TYPE_SENDER, context));
        descriptor.getOutboundRouter().addRouter(router);
        descriptor.setManagementContext(context);
        descriptor.initialise();

        return descriptor;
    }


    public static TestAgent getTestAgent()
    {
        return new TestAgent();
    }

    public static Mock getMockSession()
    {
        return new Mock(UMOSession.class, "umoSession");
    }

    public static Mock getMockMessageDispatcher()
    {
        return new Mock(UMOMessageDispatcher.class, "umoMessageDispatcher");
    }

    public static Mock getMockMessageDispatcherFactory()
    {
        return new Mock(UMOMessageDispatcherFactory.class, "umoMessageDispatcherFactory");
    }

    public static Mock getMockConnector()
    {
        return new Mock(UMOConnector.class, "umoConnector");
    }

    public static Mock getMockEvent()
    {
        return new Mock(UMOEvent.class, "umoEvent");
    }

    public static Mock getMockManagementContext()
    {
        return new Mock(ManagementContext.class, "muleManagementContext");
    }

    public static Mock getMockEndpoint()
    {
        return new Mock(UMOEndpoint.class, "umoEndpoint");
    }

    public static Mock getMockEndpointURI()
    {
        return new Mock(UMOEndpointURI.class, "umoEndpointUri");
    }

    public static Mock getMockDescriptor()
    {
        return new Mock(UMODescriptor.class, "umoDescriptor");
    }

    public static Mock getMockTransaction()
    {
        return new Mock(UMOTransaction.class, "umoTransaction");
    }

    public static Mock getMockTransactionFactory()
    {
        return new Mock(UMOTransactionFactory.class, "umoTransactionFactory");
    }

    /**
     * Creates a Mule Descriptor that can be further maniputalted by the calling
     * class before registering it with the UMOModel
     *
     * @param implementation      either a container refernece to an object or a fully
     *                            qualified class name to use as the component implementation which
     *                            event to invoke based on the evnet payload type
     * @param name                The identifying name of the component. This can be used to later
     *                            unregister it
     * @param inboundEndpointUri  The url endpointUri to listen to. Can be null
     * @param outboundEndpointUri The url endpointUri to dispatch to. Can be null
     * @param properties          properties to set on the component. Can be null
     * @throws UMOException
     */
    public static UMODescriptor createDescriptor(String implementation,
                                          String name,
                                          String inboundEndpointUri,
                                          String outboundEndpointUri,
                                          Map properties,
                                          UMOManagementContext managementContext) throws UMOException
    {
        UMOEndpointURI inEndpointUri = null;
        UMOEndpointURI outEndpointUri = null;
        if (inboundEndpointUri != null)
        {
            inEndpointUri = new MuleEndpointURI(inboundEndpointUri);
        }
        if (outboundEndpointUri != null)
        {
            outEndpointUri = new MuleEndpointURI(outboundEndpointUri);
        }

        return createDescriptor(implementation, name, inEndpointUri, outEndpointUri, properties, managementContext);
    }

    /**
     * Creates a Mule Descriptor that can be further maniputalted by the calling
     * class before registering it with the UMOModel
     *
     * @param implementation      either a container refernece to an object or a fully
     *                            qualified class name to use as the component implementation which
     *                            event to invoke based on the evnet payload type
     * @param name                The identifying name of the component. This can be used to later
     *                            unregister it
     * @param inboundEndpointUri  The url endpointUri to listen to. Can be null
     * @param outboundEndpointUri The url endpointUri to dispatch to. Can be null
     * @param properties          properties to set on the component. Can be null
     * @throws UMOException
     */
    public static UMODescriptor createDescriptor(String implementation,
                                          String name,
                                          UMOEndpointURI inboundEndpointUri,
                                          UMOEndpointURI outboundEndpointUri,
                                          Map properties,
                                          UMOManagementContext managementContext) throws UMOException
    {
        // Create the endpoints
        UMOEndpoint inboundEndpoint = null;
        UMOEndpoint outboundEndpoint = null;
        if (inboundEndpointUri != null)
        {
            inboundEndpoint = TransportFactory.createEndpoint(inboundEndpointUri,
                    UMOEndpoint.ENDPOINT_TYPE_RECEIVER, managementContext);
        }
        if (outboundEndpointUri != null)
        {
            outboundEndpoint = TransportFactory.createEndpoint(outboundEndpointUri,
                    UMOEndpoint.ENDPOINT_TYPE_SENDER, managementContext);
        }
        return createDescriptor(implementation, name, inboundEndpoint, outboundEndpoint, properties);
    }

    /**
     * Creates a Mule Descriptor that can be further maniputalted by the calling
     * class before registering it with the UMOModel
     *
     * @param implementation   either a container refernece to an object or a fully
     *                         qualified class name to use as the component implementation which
     *                         event to invoke based on the evnet payload type
     * @param name             The identifying name of the component. This can be used to later
     *                         unregister it
     * @param inboundEndpoint  The endpoint to listen to. Can be null
     * @param outboundEndpoint The endpoint to dispatch to. Can be null
     * @param properties       properties to set on the component. Can be null
     * @throws UMOException
     */
    public static UMODescriptor createDescriptor(String implementation,
                                          String name,
                                          UMOEndpoint inboundEndpoint,
                                          UMOEndpoint outboundEndpoint,
                                          Map properties) throws UMOException
    {
        MuleDescriptor descriptor = new MuleDescriptor();
        descriptor.setServiceFactory(new SimpleObjectFactory(implementation, properties));
        descriptor.setName(name);
        descriptor.setModelName(MuleProperties.OBJECT_SYSTEM_MODEL);
        if (properties != null)
        {
            descriptor.getProperties().putAll(properties);
        }

        descriptor.setOutboundRouter(new OutboundRouterCollection());
        if (outboundEndpoint != null)
        {
            OutboundPassThroughRouter router = new OutboundPassThroughRouter();
            router.addEndpoint(outboundEndpoint);
            descriptor.getOutboundRouter().addRouter(router);
        }
        descriptor.setInboundRouter(new InboundRouterCollection());
        if (inboundEndpoint != null)
        {
            descriptor.getInboundRouter().addEndpoint(inboundEndpoint);
        }

        return descriptor;
    }
}

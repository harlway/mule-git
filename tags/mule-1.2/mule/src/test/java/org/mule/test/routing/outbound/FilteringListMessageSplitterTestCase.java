/*
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) SymphonySoft Limited. All rights reserved.
 * http://www.symphonysoft.com
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.mule.test.routing.outbound;

import org.mule.impl.MuleMessage;
import org.mule.providers.AbstractConnector;
import org.mule.providers.AbstractMessageDispatcher;
import org.mule.routing.outbound.FilteringListMessageSplitter;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.tck.testmodels.fruit.Apple;
import org.mule.umo.UMOComponent;
import org.mule.umo.UMOEvent;
import org.mule.umo.UMOException;
import org.mule.umo.UMOMessage;
import org.mule.umo.UMOSession;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.umo.endpoint.UMOEndpointURI;
import org.mule.umo.provider.UMOConnector;
import org.mule.umo.provider.UMOMessageDispatcher;
import org.mule.umo.provider.UMOMessageDispatcherFactory;
import org.mule.umo.routing.RoutingException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:aperepel@itci.com">Andrew Perepelytsya</a>
 *
 * $Id$
 */
public class FilteringListMessageSplitterTestCase extends AbstractMuleTestCase
{

    public void testCorrelationGroupSizePropertySet() throws Exception
    {
        UMOComponent testComponent = getTestComponent(getTestDescriptor("test", Apple.class.getName()));
        UMOSession session = getTestSession(testComponent);

        UMOEndpoint endpoint = getTestEndpoint("Test1Provider", UMOEndpoint.ENDPOINT_TYPE_SENDER);

        FilteringListMessageSplitter router = new FilteringListMessageSplitter();
        router.setFilter(null);
        router.addEndpoint(endpoint);

        List payload = new ArrayList();
        payload.add("one");
        payload.add("two");
        payload.add("three");
        payload.add("four");

        UMOMessage message = new MuleMessage(payload);

        // need this dirty trick, otherwise "testConnector not started" error
        // is popping up
        endpoint.getConnector().setDispatcherFactory(new TestDispatcherFactory());

        UMOMessage result = router.route(message, session, true);
        assertNotNull(result);

        assertEquals("Correlation group size has not been set.",
                      4,
                      result.getCorrelationGroupSize());
    }

    private static final class TestDispatcherFactory implements UMOMessageDispatcherFactory
    {
        public UMOMessageDispatcher create(UMOConnector connector) throws UMOException
        {
            return new TestMessageDispatcher(connector);
        }

        private static class TestMessageDispatcher extends AbstractMessageDispatcher
        {
            public TestMessageDispatcher(final UMOConnector connector)
            {
                super((AbstractConnector) connector);
            }

            public void doDispose()
            {

            }

            public void doDispatch(UMOEvent event) throws Exception
            {
                if(event.getEndpoint().getEndpointURI().toString().equals("test://AlwaysFail")) {
                    throw new RoutingException(event.getMessage(), event.getEndpoint());
                }
            }

            public UMOMessage doSend(UMOEvent event) throws Exception
            {
                if(event.getEndpoint().getEndpointURI().toString().equals("test://AlwaysFail")) {
                    throw new RoutingException(event.getMessage(), event.getEndpoint());
                }
                return event.getMessage();
            }

            public UMOMessage receive(UMOEndpointURI endpointUri, long timeout) throws Exception
            {
                return null;
            }

            public Object getDelegateSession() throws UMOException
            {
                return null;
            }
        }
    }
}

/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.routing.inbound;

import org.mule.impl.MuleEvent;
import org.mule.impl.MuleMessage;
import org.mule.routing.AggregationException;
import org.mule.routing.LoggingCatchAllStrategy;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.tck.testmodels.fruit.Apple;
import org.mule.umo.UMOComponent;
import org.mule.umo.UMOEvent;
import org.mule.umo.UMOException;
import org.mule.umo.UMOMessage;
import org.mule.umo.UMOSession;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.umo.routing.UMOInboundRouterCollection;

import java.util.Iterator;
import java.util.Map;

public class EventAggregatorTestCase extends AbstractMuleTestCase
{

    public void testMessageAggregator() throws Exception
    {
        UMOComponent testComponent = getTestComponent(getTestDescriptor("test", Apple.class.getName()));
        UMOSession session = getTestSession(testComponent);

        UMOInboundRouterCollection messageRouter = new InboundRouterCollection();
        SimpleEventAggregator router = new SimpleEventAggregator(3);
        messageRouter.addRouter(router);
        messageRouter.setCatchAllStrategy(new LoggingCatchAllStrategy());

        UMOMessage message1 = new MuleMessage("test event A");
        UMOMessage message2 = new MuleMessage("test event B");
        UMOMessage message3 = new MuleMessage("test event C");

        UMOEndpoint endpoint = getTestEndpoint("Test1Provider", UMOEndpoint.ENDPOINT_TYPE_SENDER);
        UMOEvent event1 = new MuleEvent(message1, endpoint, session, false);
        UMOEvent event2 = new MuleEvent(message2, endpoint, session, false);
        UMOEvent event3 = new MuleEvent(message3, endpoint, session, false);
        assertTrue(router.isMatch(event1));
        assertTrue(router.isMatch(event2));
        assertTrue(router.isMatch(event3));

        assertNull(router.process(event1));
        assertNull(router.process(event2));

        UMOEvent[] results = router.process(event3);
        assertNotNull(results);
        assertEquals(1, results.length);
        assertEquals("test event A test event B test event C ", results[0].getMessageAsString());
    }

    public static class SimpleEventAggregator extends AbstractEventAggregator
    {
        private final int eventThreshold;
        private int eventCount = 0;

        public SimpleEventAggregator(int eventThreshold)
        {
            this.eventThreshold = eventThreshold;
        }

        protected boolean shouldAggregateEvents(EventGroup events)
        {
            eventCount++;
            if (eventCount == eventThreshold)
            {
                eventCount = 0;
                return true;
            }
            return false;
        }

        protected UMOMessage aggregateEvents(EventGroup events) throws AggregationException
        {
            if (events.size() != this.eventThreshold)
            {
                throw new IllegalStateException("eventThreshold not yet reached?");
            }

            StringBuffer newPayload = new StringBuffer(80);

            for (Iterator iterator = events.iterator(); iterator.hasNext();)
            {
                UMOEvent event = (UMOEvent)iterator.next();
                try
                {
                    newPayload.append(event.getMessageAsString()).append(" ");
                }
                catch (UMOException e)
                {
                    throw new AggregationException(events, event.getEndpoint(), e);
                }
            }

            return new MuleMessage(newPayload.toString(), (Map)null);
        }
    }
}

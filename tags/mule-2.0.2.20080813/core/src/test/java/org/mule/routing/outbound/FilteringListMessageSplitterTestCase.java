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

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.api.MuleSession;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.service.Service;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.tck.testmodels.fruit.Apple;

import java.util.ArrayList;
import java.util.List;

public class FilteringListMessageSplitterTestCase extends AbstractMuleTestCase
{
    public void testCorrelationGroupSizePropertySet() throws Exception
    {
        Service testService = getTestService("test", Apple.class);
        MuleSession session = getTestSession(testService, muleContext);

        OutboundEndpoint endpoint = getTestOutboundEndpoint("Test1Provider");

        FilteringListMessageSplitter router = new FilteringListMessageSplitter();
        router.setFilter(null);
        router.addEndpoint(endpoint);

        List payload = new ArrayList();
        payload.add("one");
        payload.add("two");
        payload.add("three");
        payload.add("four");

        MuleMessage message = new DefaultMuleMessage(payload);

        MuleMessage result = router.route(message, session, true);
        assertNotNull(result);

        assertEquals("Correlation group size has not been set.", 4, result.getCorrelationGroupSize());
    }
}

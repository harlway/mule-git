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

import org.mule.DefaultMuleEvent;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEvent;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.util.UUID;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.IteratorUtils;

public class EventGroupTestCase extends AbstractMuleTestCase
{

     public void testConcurrentIteration() throws Exception
    {
        EventGroup eg = new EventGroup(UUID.getUUID());
        assertFalse(eg.iterator().hasNext());

        // add to events to start with
        eg.addEvent(getTestEvent("foo1"));
        eg.addEvent(getTestEvent("foo2"));
        assertTrue(eg.iterator().hasNext());

        // now add events while we iterate over the group
        Iterator i = eg.iterator();
        assertNotNull(i.next());
        eg.addEvent(getTestEvent("foo3"));
        assertNotNull(i.next());
        eg.addEvent(getTestEvent("foo4"));
        assertFalse(i.hasNext());

        // the added events should be in there though
        assertEquals(4, eg.size());
    }

    public void testEquals()
    {
        EventGroup g1 = new EventGroup("foo");
        EventGroup g2 = new EventGroup("foo");
        EventGroup g3 = new EventGroup("bar");

        assertEquals(g1, g2);
        assertFalse(g1.equals(g3));

        MyEventGroup mg = new MyEventGroup("foo");
        assertEquals(g1, mg);
        assertEquals(mg, g1);

        mg = new MyEventGroup("bar");
        assertFalse(g1.equals(mg));
        assertFalse(mg.equals(g1));
    }

    public void testHashCode()
    {
        String uuid = UUID.getUUID();
        EventGroup g1 = new EventGroup(uuid);
        EventGroup g2 = new EventGroup(uuid);
        EventGroup g3 = new EventGroup(UUID.getUUID());

        assertEquals(g1.hashCode(), g2.hashCode());
        assertEquals(g1, g2);

        assertFalse(g1.hashCode() == g3.hashCode());
        assertFalse(g1.equals(g3));
        assertFalse(g3.equals(g1));

        // now test Set compatibility
        Set s = new HashSet();
        s.add(g1);

        // make sure g1 is in the set
        assertTrue(s.contains(g1));
        assertEquals(1, s.size());

        // g2 has the same hash, so it should match
        assertTrue(s.contains(g2));
        // even though there is only one object in the set
        assertEquals(1, s.size());

        // make sure g3 cannot be found
        assertFalse(s.contains(g3));
        // now add it
        assertTrue(s.add(g3));
        // make sure it is in there
        assertTrue(s.contains(g3));
        // make sure it is really in there
        assertEquals(2, s.size());
    }

    public void testCompareTo() throws InterruptedException
    {
        String uuid = UUID.getUUID();
        EventGroup g1 = new EventGroup(uuid);
        EventGroup g2 = new EventGroup(uuid);
        EventGroup g3 = new EventGroup(UUID.getUUID());
        
        // test comparison against null
        try
        {
            g1.compareTo(null);
            fail("expected NullPointerException");
        }
        catch (NullPointerException npe)
        {
            // expected
        }

        // test comparison against incompatible object
        try
        {
            g1.compareTo("foo");
            fail("expected ClassCastException");
        }
        catch (ClassCastException cce)
        {
            // expected
        }

        // these tests use the groupId
        assertNotNull(g1);
        assertNotNull(g2);
        assertNotNull(g3);
        
        assertEquals(0, g1.compareTo(g2));
        /*
         * guids are randomly generated, we cannot compare them with '<' '>'
         * we used to generate them this way: generator.generateTimeBasedUUID().toString()
         * but now we generate them as java.util.UUID.randomUUID().toString() 
         */               
        assertTrue(g1.compareTo(g3) != 0);
        assertTrue(g3.compareTo(g1) != 0);
        assertTrue(g3.compareTo(g2) != 0);

        // when the groupId is not Comparable, the creation time is used as fallback
        g1 = new EventGroup(new Object());
        g2 = new EventGroup(new Object());
        // g1 is older (smaller) than g2
        assertTrue(g1.compareTo(g2) < 0);
        assertTrue(g2.compareTo(g1) > 0);
    }

    public void testToArray() throws Exception
    {
        EventGroup eg = new EventGroup(UUID.getUUID());
        eg.addEvent(getTestEvent("foo1"));
        eg.addEvent(getTestEvent("foo2"));

        Object[] array1 = IteratorUtils.toArray(eg.iterator());
        MuleEvent[] array2 = eg.toArray();
        assertTrue(Arrays.equals(array1, array2));
    }

    public void testToString() throws Exception
    {
        EventGroup eg = new EventGroup(UUID.getUUID());
        String es = eg.toString();
        assertTrue(es.endsWith("events=0}"));

        MuleEvent e = getTestEvent("foo");
        eg.addEvent(e);
        es = eg.toString();
        assertTrue(es.indexOf("events=1") != -1);
        assertTrue(es.endsWith("[" + e.getMessage().getUniqueId() + "]}"));

        MuleEvent e2 = new DefaultMuleEvent(new DefaultMuleMessage("foo2", muleContext), e);
        eg.addEvent(e2);
        es = eg.toString();
        assertTrue(es.indexOf("events=2") != -1);
        assertTrue(es.endsWith(e.getMessage().getUniqueId() + ", " + e2.getMessage().getUniqueId() + "]}"));
    }

    private static class MyEventGroup extends EventGroup
    {
        private static final long serialVersionUID = 1L;

        public MyEventGroup(Object groupId)
        {
            super(groupId);
        }

        public MyEventGroup(Object groupId, int expectedSize)
        {
            super(groupId, expectedSize);
        }
    }

}

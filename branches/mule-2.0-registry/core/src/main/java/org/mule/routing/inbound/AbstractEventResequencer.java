/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.routing.inbound;

import org.mule.umo.MessagingException;
import org.mule.umo.UMOEvent;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentMap;

import java.util.Arrays;
import java.util.Comparator;

/**
 * <code>AbstractEventResequencer</code> is used to receive a set of events,
 * resequence them and forward them on to their destination
 */

// TODO HH: much of the code here (like the spinloop) is *exactly* the same as in
// AbstractEventAggregator, obviously we should unify this
public abstract class AbstractEventResequencer extends SelectiveConsumer
{
    protected static final String NO_CORRELATION_ID = "no-id";

    private final ConcurrentMap eventGroups = new ConcurrentHashMap();
    private volatile Comparator comparator;

    public AbstractEventResequencer()
    {
        super();
    }

    public Comparator getComparator()
    {
        return comparator;
    }

    public void setComparator(Comparator eventComparator)
    {
        this.comparator = eventComparator;
    }

    // @Override
    public UMOEvent[] process(UMOEvent event) throws MessagingException
    {
        UMOEvent[] result = null;

        if (this.isMatch(event))
        {
            // indicates interleaved EventGroup removal (very rare)
            boolean miss = false;

            // match event to its group
            final Object groupId = this.getEventGroupIdForEvent(event);

            // spinloop for the EventGroup lookup
            while (true)
            {
                if (miss)
                {
                    // might be a nop but it's not that important
                    Thread.yield();
                }

                // check for an existing group first
                EventGroup group = this.getEventGroupWithId(groupId);

                // does the group exist?
                if (group == null)
                {
                    // ..apparently not, so create a new one & add it
                    group = this.addEventGroup(this.createEventGroup(event, groupId));
                }

                // ensure that only one thread at a time evaluates this EventGroup
                synchronized (group)
                {
                    // make sure no other thread removed the group in the meantime
                    if (group != this.getEventGroupWithId(groupId))
                    {
                        // if that is the (rare) case, spin
                        miss = true;
                        continue;
                    }

                    // add the incoming event to the group
                    group.addEvent(event);

                    if (this.shouldResequenceEvents(group))
                    {
                        result = this.resequenceEvents(group);
                        this.removeEventGroup(group);
                    }

                    // result or not: exit spinloop
                    break;
                }
            }
        }

        return result;
    }

    /**
     * TODO HH: writeme
     * 
     * @param event
     * @param correlationId
     * @return
     */
    protected EventGroup createEventGroup(UMOEvent event, Object groupId)
    {
        return new EventGroup(groupId);
    }

    /**
     * TODO HH: writeme
     * 
     * @param event
     * @return
     */
    protected Object getEventGroupIdForEvent(UMOEvent event)
    {
        String groupId = event.getMessage().getCorrelationId();

        if (groupId == null)
        {
            groupId = NO_CORRELATION_ID;
        }

        return groupId;
    }

    /**
     * TODO HH: writeme
     * 
     * @param groupId
     * @return
     */
    protected EventGroup getEventGroupWithId(Object groupId)
    {
        return (EventGroup)eventGroups.get(groupId);
    }

    /**
     * TODO HH: writeme
     * 
     * @param group
     * @return
     */
    protected EventGroup addEventGroup(EventGroup group)
    {
        EventGroup previous = (EventGroup)eventGroups.putIfAbsent(group.getGroupId(), group);
        // a parallel thread might have removed the EventGroup already,
        // therefore we need to validate our current reference
        return (previous != null ? previous : group);
    }

    /**
     * TODO HH: writeme
     * 
     * @param group
     */
    protected void removeEventGroup(EventGroup group)
    {
        eventGroups.remove(group.getGroupId());
    }

    /**
     * TODO HH: writeme
     * 
     * @param events
     * @return
     */
    protected UMOEvent[] resequenceEvents(EventGroup events)
    {
        if (events == null || events.size() == 0)
        {
            return EventGroup.EMPTY_EVENTS_ARRAY;
        }

        UMOEvent[] result = events.toArray();
        Comparator cmp = this.getComparator();

        if (cmp != null)
        {
            Arrays.sort(result, cmp);
        }
        else
        {
            logger.debug("Event comparator is null, events were not reordered");
        }

        return result;
    }

    /**
     * TODO HH: writeme
     * 
     * @param events
     * @return
     */
    protected abstract boolean shouldResequenceEvents(EventGroup events);

}

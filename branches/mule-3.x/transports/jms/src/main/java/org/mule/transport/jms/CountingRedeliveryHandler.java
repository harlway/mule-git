/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.jms;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.transport.jms.i18n.JmsMessages;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This redelivery handler will keep counting the redelivery attempts for each message redelivered. Used for
 * providers not implementing the {@code JMSXDeliveryCount} property support.
 */
public class CountingRedeliveryHandler extends AbstractRedeliveryHandler
{
    /**
     * logger used by this class
     */
    protected static final Log logger = LogFactory.getLog(CountingRedeliveryHandler.class);

    private Map<String, Integer> messages = null;

    @SuppressWarnings("unchecked")
    public CountingRedeliveryHandler()
    {
        super();
        messages = Collections.synchronizedMap(new LRUMap(256));
    }

    /**
     * process the redelivered message. If the Jms receiver should process the
     * message, it should be returned. Otherwise the connector should throw a
     * <code>MessageRedeliveredException</code> to indicate that the message should
     * be handled by the connector Exception Handler.
     * 
     */
    @Override
    public void handleRedelivery(Message message) throws JMSException, MuleException
    {
        final int connectorRedelivery = connector.getMaxRedelivery();
        if (connectorRedelivery == JmsConnector.REDELIVERY_IGNORE || connectorRedelivery < 0 ) // just in case, for manual setting)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("We were asked to ignore the redelivery count, nothing to do here.");
            }
            return;
        }

        String id = message.getJMSMessageID();

        if (id == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Message doesn't have a JMSMessageID set, Mule can't handle redelivery for it. " + message);
            }
            return;
        }

        Integer redeliveryCount = messages.remove(id);
        if (redeliveryCount != null)
        {
            redeliveryCount += 1; // inc the count
        }

        if (redeliveryCount == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Message with id: " + id + " has been redelivered for the first time");
            }
            messages.put(id, 1);
        }
        else if (redeliveryCount == 1)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Message with id: " + id + " has been redelivered for the first time");
            }

            if (connectorRedelivery == JmsConnector.REDELIVERY_FAIL_ON_FIRST)
            {
                MuleMessage msg = createMuleMessage(message);
                throw new MessageRedeliveredException(
                    JmsMessages.tooManyRedeliveries(id, String.valueOf(redeliveryCount), 
                        connectorRedelivery, connector.getName()), msg);
            }
        }
        else if (redeliveryCount > connectorRedelivery)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug(MessageFormat.format(
                        "Message with id: {0} has been redelivered {1} times, which exceeds the maxRedelivery setting " +
                        "of {2} on the connector {3}", id, redeliveryCount, connectorRedelivery, connector.getName()));
            }
            MuleMessage msg = createMuleMessage(message);
            throw new MessageRedeliveredException(
                JmsMessages.tooManyRedeliveries(id, "" + redeliveryCount, connectorRedelivery,
                    connector.getName()), msg);
        }
        else
        {
            messages.put(id, redeliveryCount);
            if (logger.isDebugEnabled())
            {
                logger.debug("Message with id: " + id + " has been redelivered " + redeliveryCount + " times");
            }
        }
    }
}

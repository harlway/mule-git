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
 *
 */

package org.mule.providers.vm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.config.i18n.Message;
import org.mule.config.i18n.Messages;
import org.mule.impl.MuleMessage;
import org.mule.providers.AbstractMessageDispatcher;
import org.mule.providers.streaming.StreamMessageAdapter;
import org.mule.umo.UMOEvent;
import org.mule.umo.UMOException;
import org.mule.umo.UMOMessage;
import org.mule.umo.endpoint.UMOEndpointURI;
import org.mule.umo.provider.DispatchException;
import org.mule.umo.provider.NoReceiverForEndpointException;
import org.mule.umo.provider.UMOConnector;
import org.mule.util.Utility;
import org.mule.util.queue.Queue;
import org.mule.util.queue.QueueSession;

import java.io.ByteArrayInputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * <code>VMMessageDispatcher</code> is used for providing in memory interaction between
 * components.
 * 
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @author <a href="mailto:gnt@codehaus.org">Guillaume Nodet</a>
 * @version $Revision$
 */
public class VMMessageDispatcher extends AbstractMessageDispatcher
{
    /**
     * logger used by this class
     */
    private static transient Log logger = LogFactory.getLog(VMMessageDispatcher.class);

    private VMConnector connector;

    public VMMessageDispatcher(VMConnector connector)
    {
        super(connector);
        this.connector = connector;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.umo.provider.UMOMessageDispatcher#getDelegateSession()
     */
    public Object getDelegateSession() throws UMOException
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.umo.provider.UMOMessageDispatcher#receive(java.lang.String,
     *      org.mule.umo.UMOEvent)
     */
    public UMOMessage receive(UMOEndpointURI endpointUri, long timeout) throws Exception
    {
        if (!connector.isQueueEvents()) {
            throw new UnsupportedOperationException("Receive only supported on the VM Queue Connector");
        }
        QueueSession queueSession = null;
        try {
            queueSession = connector.getQueueSession();
            Queue queue = queueSession.getQueue(endpointUri.getAddress());
            if (queue == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("No queue with name " + endpointUri.getAddress());
                }
                return null;
            } else {
                UMOEvent event = null;
                if (logger.isDebugEnabled()) {
                    logger.debug("Waiting for a message on " + endpointUri.getAddress());
                }
                try {
                    event = (UMOEvent) queue.poll(timeout);
                } catch (InterruptedException e) {
                    logger.error("Failed to receive event from queue: " + endpointUri);
                }
                if (event != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Event received: " + event);
                    }
                    return event.getMessage();
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("No event received after " + timeout + " ms");
                    }
                    return null;
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.umo.provider.UMOConnector#dispatch(org.mule.umo.UMOEvent)
     */
    public void doDispatch(UMOEvent event) throws Exception
    {
        UMOEndpointURI endpointUri = event.getEndpoint().getEndpointURI();

        if (endpointUri == null) {
            throw new DispatchException(new Message(Messages.X_IS_NULL, "Endpoint"),
                                        event.getMessage(),
                                        event.getEndpoint());
        }
        if (connector.isQueueEvents()) {
            QueueSession session = connector.getQueueSession();
            Queue queue = session.getQueue(endpointUri.getAddress());
            queue.put(event);
        } else {
            VMMessageReceiver receiver = connector.getReceiver(event.getEndpoint().getEndpointURI());
            if (receiver == null) {
                logger.warn("No receiver for endpointUri: " + event.getEndpoint().getEndpointURI());
                return;
            }

            if(event.isStreaming()) {
                StreamMessageAdapter sma = (StreamMessageAdapter)event.getMessage().getAdapter();
                sma.setIn(new PipedInputStream());
                PipedOutputStream out = new PipedOutputStream((PipedInputStream)sma.getInput());
                sma.write(event, out);
            }
            receiver.onEvent(event);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("dispatched Event on endpointUri: " + endpointUri);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.umo.provider.UMOConnector#send(org.mule.umo.UMOEvent)
     */
    public UMOMessage doSend(UMOEvent event) throws Exception
    {
        UMOMessage retMessage = null;
        UMOEndpointURI endpointUri = event.getEndpoint().getEndpointURI();
        VMMessageReceiver receiver = connector.getReceiver(endpointUri);
        if (receiver == null) {
            if (connector.isQueueEvents()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Writing to queue as there is no receiver on connector: " + connector.getName()
                            + ", for endpointUri: " + event.getEndpoint().getEndpointURI());
                }
                doDispatch(event);
                return null;
            } else {
                throw new NoReceiverForEndpointException(new Message(Messages.NO_RECEIVER_X_FOR_ENDPOINT_X,
                                                                     connector.getName(),
                                                                     event.getEndpoint().getEndpointURI()));
            }
        }
        if(event.isStreaming()) {
            StreamMessageAdapter sma = (StreamMessageAdapter)event.getMessage().getAdapter();
            sma.setIn(new PipedInputStream());
            PipedOutputStream out = new PipedOutputStream((PipedInputStream)sma.getInput());
            sma.write(event, out);
        }

        retMessage = (UMOMessage) receiver.onCall(event);

        if(event.isStreaming() && retMessage!=null) {
            StreamMessageAdapter sma = (StreamMessageAdapter)event.getMessage().getAdapter();
            sma.setResponse(new ByteArrayInputStream(Utility.objectToByteArray(retMessage.getPayload())));
            retMessage = new MuleMessage(sma, retMessage.getProperties());
        }
        logger.debug("sent event on endpointUri: " + event.getEndpoint().getEndpointURI());
        return retMessage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.umo.provider.UMOMessageDispatcher#getConnector()
     */
    public UMOConnector getConnector()
    {
        return connector;
    }

    public void doDispose()
    {
    }
}

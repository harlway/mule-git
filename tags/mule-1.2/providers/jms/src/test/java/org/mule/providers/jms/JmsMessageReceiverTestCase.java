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

package org.mule.providers.jms;

import com.mockobjects.dynamic.Mock;
import org.mule.MuleManager;
import org.mule.impl.MuleDescriptor;
import org.mule.impl.endpoint.MuleEndpoint;
import org.mule.tck.providers.AbstractMessageReceiverTestCase;
import org.mule.tck.testmodels.fruit.Orange;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.umo.provider.UMOConnector;
import org.mule.umo.provider.UMOMessageReceiver;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;

/**
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */

public class JmsMessageReceiverTestCase extends AbstractMessageReceiverTestCase
{
    private JmsConnector connector;

    protected void doSetUp() throws Exception {
        MuleManager.getInstance().registerConnector(getConnector());
        super.doSetUp();
    }

    public void testReceive() throws Exception
    {
        JmsMessageReceiver receiver = (JmsMessageReceiver) getMessageReceiver();
        assertNotNull(receiver.getComponent());
        assertNotNull(receiver.getConnector());
        assertNotNull(receiver.getEndpoint());
        // hmm how do we unit test a message receive
        // receiver.onMessage((Message) getValidMessage());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.tck.providers.AbstractMessageReceiverTestCase#getMessageReceiver()
     */
    public UMOMessageReceiver getMessageReceiver() throws Exception
    {
        MuleDescriptor descriptor = getTestDescriptor("orange", Orange.class.getName());
        return new JmsMessageReceiver(endpoint.getConnector(), getTestComponent(descriptor), endpoint);
    }

    public UMOConnector getConnector() throws Exception
    {
        if (connector == null) {
            connector = new JmsConnector();
            connector.setName("TestConnector");
            connector.setSpecification("1.1");

            Mock connectionFactory = new Mock(ConnectionFactory.class);
            Mock connection = new Mock(Connection.class);
            connectionFactory.expectAndReturn("createConnection", connection.proxy());
            connection.expect("close");
            connection.expect("start");
            connection.expect("stop");
            connector.setConnectionFactory((ConnectionFactory) connectionFactory.proxy());
        }
        return connector;
    }

    public Object getValidMessage() throws Exception
    {
        return JmsConnectorTestCase.getMessage();
    }

    public UMOEndpoint getEndpoint() throws Exception
    {
        endpoint = new MuleEndpoint("jms://testcase", true);
        endpoint.setConnector(getConnector());
        return endpoint;
    }
}

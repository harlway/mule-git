package org.mule.test.integration.providers.jms.oracle;

import oracle.AQ.AQException;

import org.mule.providers.oracle.jms.util.AQUtil;
import org.mule.providers.oracle.jms.util.MuleUtil;
import org.mule.umo.UMOException;

import javax.jms.JMSException;

/**
 * Tests the connector against a live Oracle database using standard JMS messages.
 * 
 * @author <a href="mailto:carlson@hotpop.com">Travis Carlson</a>
 */
public class StandardPayloadIntegrationTestCase extends AbstractIntegrationTestCase {
    
    protected String getConfigurationFiles() {
    	return "jms-connector-config.xml";
    }

    public void testCreateAndDropQueue() throws AQException, JMSException {	    
	   AQUtil.createOrReplaceQueue(session, connector.getUsername(), TestConfig.QUEUE_RAW, "RAW");
	   AQUtil.dropQueue(session, connector.getUsername(), TestConfig.QUEUE_RAW, /*force*/false);
	} 
    
	public void testTextMessage() throws JMSException, AQException, UMOException {
	    AQUtil.createOrReplaceTextQueue(session, connector.getUsername(), TestConfig.QUEUE_TEXT);

	    MuleUtil.sendMessage("jms://" + TestConfig.QUEUE_TEXT, TestConfig.TEXT_MESSAGE);
        assertEquals(TestConfig.TEXT_MESSAGE, MuleUtil.receiveMessage("jms://" + TestConfig.QUEUE_TEXT, "JMSMessageToObject"));
 	    
        AQUtil.dropQueue(session, connector.getUsername(), TestConfig.QUEUE_TEXT, /*force*/false);
    }
}

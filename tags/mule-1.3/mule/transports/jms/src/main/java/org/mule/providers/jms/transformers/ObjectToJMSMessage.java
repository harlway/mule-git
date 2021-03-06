/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.jms.transformers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.umo.transformer.TransformerException;

/**
 * <code>ObjectToJMSMessage</code> will convert any object to a
 * <code>javax.jms.Message</code> or sub-type into an object. One of the 5
 * types of JMS message will be created based on the type of Object passed in.
 * <ul>
 * <li>java.lang.String - javax.jms.TextMessage</li>
 * <li>byte[] - javax.jms.BytesMessage</li>
 * <li>java.util.Map - javax.jms.MapMessage</li>
 * <li>java.io.InputStream - javax.jms.StreamMessage</li>
 * <li>javalang.Object - javax.jms.ObjectMessage</li>
 * </ul>
 * Note that if compression is turned on then a
 * <code>javax.jms.BytesMessage</code> is always sent
 * 
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */

public class ObjectToJMSMessage extends AbstractJmsTransformer
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 6900208781863624801L;

    /**
     * logger used by this class
     */
    private static transient Log logger = LogFactory.getLog(ObjectToJMSMessage.class);

    public Object doTransform(Object src, String encoding) throws TransformerException
    {
        Object result = null;

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Source object is " + src.getClass().getName());
            }

            result = transformToMessage(src);

            if (logger.isDebugEnabled()) {
                logger.debug("Resulting object is " + result.getClass().getName());
            }
        } catch (Exception e) {
            throw new TransformerException(this, e);
        }
        return result;
    }

}

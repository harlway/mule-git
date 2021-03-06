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
 
package org.mule.samples.errorhandler.exceptions;

import org.mule.config.i18n.Message;
import org.mule.umo.UMOException;

/**
 *  <code>BusinessException</code> TODO (document class)
 *
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class BusinessException extends UMOException
{
    /**
     * @param message
     */
    public BusinessException(String message)
    {
        this("BUSINESS EXCEPTION: " + message, null);
    }

    /**
     * @param message
     * @param cause
     */
    public BusinessException(String message, Throwable cause)
    {
        super(Message.createStaticMessage(message), cause);
    }

}

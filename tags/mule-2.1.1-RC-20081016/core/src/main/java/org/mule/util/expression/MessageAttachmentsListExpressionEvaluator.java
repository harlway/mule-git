/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.util.expression;

import org.mule.api.transport.MessageAdapter;
import org.mule.config.i18n.CoreMessages;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Looks up the attachment(s) on the message using the expression given. The expression can contain a comma-separated list
 * of header names to lookup. A {@link java.util.List} of values is returned.
 *
 * @see MessageAttachmentsExpressionEvaluator
 * @see MessageAttachmentExpressionEvaluator
 * @see ExpressionEvaluator
 * @see ExpressionEvaluatorManager
 */
public class MessageAttachmentsListExpressionEvaluator implements ExpressionEvaluator
{
    public static final String NAME = "attachments-list";
    public static final String DELIM = ",";

    public Object evaluate(String expression, Object message)
    {
        boolean required = false;

        //This is a bit of a hack to manage required headers
        if(expression.endsWith("required"))
        {
            required = true;
            expression = expression.substring(0, expression.length() - 8);
        }
        
        if (message instanceof MessageAdapter)
        {

            StringTokenizer tokenizer = new StringTokenizer(expression, DELIM);
            List result = new ArrayList(tokenizer.countTokens());
            while(tokenizer.hasMoreTokens())
            {
                String s = tokenizer.nextToken();
                s = s.trim();
                Object val = ((MessageAdapter) message).getAttachment(s);
                if (val != null)
                {
                    result.add(val);
                }
                else if(required)
                {
                    throw new ExpressionRuntimeException(CoreMessages.expressionEvaluatorReturnedNull(NAME, expression));
                }
            }
            if(result.size()==0)
            {
                return null;
            }
            else
            {
                return result;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return NAME;
    }

    /** {@inheritDoc} */
    public void setName(String name)
    {
        throw new UnsupportedOperationException("setName");
    }
}
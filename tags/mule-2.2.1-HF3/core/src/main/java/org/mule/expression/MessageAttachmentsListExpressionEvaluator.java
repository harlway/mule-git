/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.expression;

import org.mule.api.MuleMessage;
import org.mule.api.expression.ExpressionEvaluator;
import org.mule.api.expression.RequiredValueException;
import org.mule.config.i18n.CoreMessages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Looks up the attachment(s) on the message using the expression given. The expression can contain a comma-separated list
 * of header names to lookup. A {@link java.util.List} of values is returned.
 *
 * @see MessageAttachmentsExpressionEvaluator
 * @see MessageAttachmentExpressionEvaluator
 * @see org.mule.api.expression.ExpressionEvaluator
 * @see DefaultExpressionManager
 */
public class MessageAttachmentsListExpressionEvaluator implements ExpressionEvaluator
{
    public static final String NAME = "attachments-list";
    public static final String DELIM = ",";

    public static final String ALL_ARGUMENT = "{all}";

    public Object evaluate(String expression, MuleMessage message)
    {
        boolean required;

        List result;
        if (ALL_ARGUMENT.equals(expression))
        {
            result = new ArrayList(message.getAttachmentNames().size());
            for (Iterator iterator = message.getAttachmentNames().iterator(); iterator.hasNext();)
            {
                String name = (String) iterator.next();
                result.add(message.getAttachment(name));
            }
        }
        else
        {
            StringTokenizer tokenizer = new StringTokenizer(expression, DELIM);
            result = new ArrayList(tokenizer.countTokens());
            while (tokenizer.hasMoreTokens())
            {
                String s = tokenizer.nextToken();
                s = s.trim();
                if (s.endsWith("*"))
                {
                    s = s.substring(s.length() - 1);
                    required = false;
                }
                else
                {
                    required = true;
                }
                Object val = message.getAttachment(s);
                if (val != null)
                {
                    result.add(val);
                }
                else if (required)
                {
                    throw new RequiredValueException(CoreMessages.expressionEvaluatorReturnedNull(NAME, expression));
                }
            }
        }
        if (result.size() == 0)
        {
            return null;
        }
        else
        {
            return result;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return NAME;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name)
    {
        throw new UnsupportedOperationException("setName");
    }
}
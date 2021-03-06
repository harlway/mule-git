/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.samples.loanbroker.esb.transformers;

import org.mule.samples.loanbroker.esb.message.LoanQuoteRequest;
import org.mule.transformers.AbstractTransformer;
import org.mule.umo.transformer.TransformerException;

/**
 * Extracts the customer information from the request
 * 
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class LoanQuoteRequestToCustomer extends AbstractTransformer
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 3989958201499427076L;

    public LoanQuoteRequestToCustomer()
    {
        registerSourceType(LoanQuoteRequest.class);
    }

    public Object doTransform(Object src, String encoding) throws TransformerException
    {

        return ((LoanQuoteRequest)src).getCustomerRequest().getCustomer();
    }
}

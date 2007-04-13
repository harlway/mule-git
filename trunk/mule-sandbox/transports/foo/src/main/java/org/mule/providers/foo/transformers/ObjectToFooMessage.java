/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.foo.transformers;

import org.mule.transformers.AbstractEventAwareTransformer;
import org.mule.umo.transformer.TransformerException;
import org.mule.umo.UMOEventContext;

/**
 *
 * <code>ObjectToFooMessage</code> Todo Document
 *
 */
public class ObjectToFooMessage extends AbstractEventAwareTransformer
{

    public ObjectToFooMessage()
    {
        //IMPLEMENTATION NOTE:
        //Here you can set default types that the transformer will accept at runtime.
        //Mule will then validate the transformer at runtime. You can register one or
        //more source types.

        //registerSourceType(XXX.class.getName());

        //IMPLEMENTATION NOTE:
        //It's good practice to set the expected return type for this transformer here
        //This helps Mule validate event flows and Transformer chains

        //setReturnClass(YYY.class);

    }

	public Object transform(Object src, String encoding, UMOEventContext context) throws TransformerException
    {
		//IMPLEMENTATION NOTE:
        //If you only have a single source type registered you can cast the 'src' object
        //directly to that type, otherwise you need to check the instance type of 'src'
        //for each registered source type

        //Todo Transformer the source object here.
        // You have access to the Current message (including
        //headers and attachments from the context.getMessage())

        //Make sure you return a transfromed object that matches the returnClass type

        throw new UnsupportedOperationException("transform");

    }
}

/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.tck.testmodels.fruit;

import org.mule.umo.lifecycle.InitialisationException;
import org.mule.util.object.ObjectFactory;

/**
 * <code>BananaFactory</code> is a test factory that creates Bananas
 */
public class BananaFactory implements ObjectFactory
{
    public void initialise() throws InitialisationException
    {
        // nothing to do
    }
    
    public void dispose()
    {
        // nothing to do
    }

    public Object create() throws Exception
    {
        return new Banana();
    }
}

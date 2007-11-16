/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.routing.filters;

import org.mule.tck.AbstractMuleTestCase;

public class EqualsFilterTestCase extends AbstractMuleTestCase
{

    public void testEqualsFilterNoPattern()
    {
        EqualsFilter filter = new EqualsFilter();
        assertNull(filter.getPattern());
        assertFalse(filter.accept("foo"));

        filter.setPattern("foo");
        assertTrue(filter.accept("foo"));

        filter.setPattern(null);
        assertFalse(filter.accept("foo"));
    }

    public void testEqualsFilter()
    {
        Exception obj = new Exception("test");
        EqualsFilter filter = new EqualsFilter(obj);
        assertNotNull(filter.getPattern());
        assertTrue(filter.accept(obj));
        assertTrue(!filter.accept(new Exception("tes")));

        filter.setPattern("Hello");
        assertTrue(filter.accept("Hello"));
        assertTrue(!filter.accept("Helo"));
    }

}

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
 */
package org.mule.test.filters;

import org.mule.impl.MuleMessage;
import org.mule.routing.filters.WildcardFilter;
import org.mule.routing.filters.logic.AndFilter;
import org.mule.routing.filters.logic.NotFilter;
import org.mule.routing.filters.logic.OrFilter;
import org.mule.tck.NamedTestCase;

/**
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */

public class LogicFiltersTestCase extends NamedTestCase
{
    public void testAndFilter()
    {
        WildcardFilter left = new WildcardFilter("blah.blah.*");
        WildcardFilter right = new WildcardFilter("blah.*");
        AndFilter filter = new AndFilter(left, right);
        assertNotNull(filter.getLeftFilter());
        assertNotNull(filter.getRightFilter());

        assertTrue(filter.accept(new MuleMessage("blah.blah.blah", null)));
        assertTrue(right.accept(new MuleMessage("blah.blah", null)));
        assertTrue(!left.accept(new MuleMessage("blah.blah", null)));
        assertTrue(!filter.accept(new MuleMessage("blah.blah", null)));

        filter = new AndFilter();
        filter.setLeftFilter(left);
        filter.setRightFilter(right);

        assertTrue(filter.accept(new MuleMessage("blah.blah.blah", null)));
        assertTrue(!filter.accept(new MuleMessage("blah.blah", null)));
    }

    public void testOrFilter()
    {
        WildcardFilter left = new WildcardFilter("blah.blah.*");
        WildcardFilter right = new WildcardFilter("blah.b*");
        OrFilter filter = new OrFilter(left, right);
        assertNotNull(filter.getLeftFilter());
        assertNotNull(filter.getRightFilter());

        assertTrue(filter.accept(new MuleMessage("blah.blah.blah", null)));
        assertTrue(right.accept(new MuleMessage("blah.blah", null)));
        assertTrue(!left.accept(new MuleMessage("blah.blah", null)));
        assertTrue(filter.accept(new MuleMessage("blah.blah", null)));
        assertTrue(!filter.accept(new MuleMessage("blah.x.blah", null)));

        filter = new OrFilter();
        filter.setLeftFilter(left);
        filter.setRightFilter(right);

        assertTrue(filter.accept(new MuleMessage("blah.blah.blah", null)));
        assertTrue(filter.accept(new MuleMessage("blah.blah", null)));
        assertTrue(!filter.accept(new MuleMessage("blah.x.blah", null)));
    }

    public void testNotFilter()
    {
        WildcardFilter filter = new WildcardFilter("blah.blah.*");
        NotFilter notFilter = new NotFilter(filter);
        assertNotNull(notFilter.getFilter());

        assertTrue(filter.accept(new MuleMessage("blah.blah.blah", null)));
        assertTrue(!notFilter.accept(new MuleMessage("blah.blah.blah", null)));

        notFilter = new NotFilter();
        notFilter.setFilter(filter);
        assertTrue(filter.accept(new MuleMessage("blah.blah.blah", null)));
        assertTrue(!notFilter.accept(new MuleMessage("blah.blah.blah", null)));
    }
}

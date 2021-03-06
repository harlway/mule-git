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
package org.mule.test.filters.xml;

import org.apache.commons.io.IOUtils;
import org.mule.impl.MuleMessage;
import org.mule.routing.filters.xml.JXPathFilter;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.transformers.xml.XmlToDomDocument;

import java.io.InputStream;

/**
 * @author <a href="mailto:S.Vanmeerhaege@gfdi.be">Vanmeerhaeghe St�phane</a>
 * @version $Revision$
 */
public class JXPathFilterTestCase extends AbstractMuleTestCase
{
    private String xmlData = null;

    private JXPathFilter myFilter = null;
    private XmlToDomDocument transformer = null;

    protected void doSetUp() throws Exception {
        // Read Xml file
        final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        final InputStream is = currentClassLoader.getResourceAsStream("cdcatalog.xml");
        assertNotNull("Test resource not found.", is);

        xmlData = IOUtils.toString(is);

        // new UMOFilter
        myFilter = new JXPathFilter();
        transformer = new XmlToDomDocument();
    }

    public void testFilter1() throws Exception
    {
        Object obj = transformer.transform(xmlData);

        myFilter.setExpression("catalog/cd[3]/title");
        myFilter.setValue("Greatest Hits");
        boolean res = myFilter.accept(new MuleMessage(obj));
        assertTrue(res);

    }

    public void testFilter2() throws Exception
    {
        Object obj = transformer.transform(xmlData);
        myFilter.setExpression("(catalog/cd[3]/title) ='Greatest Hits'");
        boolean res = myFilter.accept(new MuleMessage(obj));
        assertTrue(res);

    }

    public void testFilter3() throws Exception
    {
        Object obj = transformer.transform(xmlData);
        myFilter.setExpression("count(catalog/cd) = 26");
        boolean res = myFilter.accept(new MuleMessage(obj));
        assertTrue(res);

    }

    public void testFilter4() throws Exception
    {
        Dummy d = new Dummy();
        d.setId(10);
        d.setContent("hello");

        myFilter.setExpression("id=10 and content='hello'");
        boolean res = myFilter.accept(new MuleMessage(d));
        assertTrue(res);

    }
}

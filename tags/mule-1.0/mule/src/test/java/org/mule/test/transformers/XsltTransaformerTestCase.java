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
package org.mule.test.transformers;

import org.mule.tck.AbstractTransformerTestCase;
import org.mule.transformers.xml.XsltTransformer;
import org.mule.umo.transformer.UMOTransformer;
import org.mule.util.Utility;

/**
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class XsltTransaformerTestCase extends AbstractTransformerTestCase {

    private String srcData;
    private String resultData;

    protected void setUp() throws Exception
    {
        super.setUp();

        srcData = Utility.loadResourceAsString("cdcatalog.xml", getClass());
        resultData = Utility.loadResourceAsString("cdcatalog.html", getClass());
    }

    public UMOTransformer getTransformer() throws Exception
    {
        XsltTransformer transformer = new XsltTransformer();
		transformer.setXslFile("cdcatalog.xsl");
		transformer.initialise();
        return transformer;
    }

    public UMOTransformer getRoundTripTransformer() throws Exception
    {
        return null;
    }

    public void testRoundtripTransform() throws Exception {
        // disable this test
    }

    public Object getTestData()
    {
        return srcData;
    }

    public Object getResultData()
    {
        return resultData;
    }

    public boolean compareResults(Object src, Object result) {
        if(src!=null) {
            src = ((String)src).replaceAll("\r", "");
        }
        return super.compareResults(src, result);
    }
}
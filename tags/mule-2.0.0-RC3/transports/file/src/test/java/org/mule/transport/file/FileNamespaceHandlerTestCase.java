/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.file;

import org.mule.tck.FunctionalTestCase;
import org.mule.transport.file.FileConnector;
import org.mule.transport.file.FilenameParser;

public class FileNamespaceHandlerTestCase extends FunctionalTestCase
{
    protected String getConfigResources()
    {
        return "file-namespace-config.xml";
    }

    public void testConfig() throws Exception
    {
        FileConnector c = (FileConnector)muleContext.getRegistry().lookupConnector("fileConnector");
        assertNotNull(c);
        
        assertEquals(1234, c.getFileAge());
        assertEquals("abc", c.getMoveToDirectory());
        assertEquals("bcd", c.getMoveToPattern());
        assertEquals("cde", c.getOutputPattern());
        assertEquals(2345, c.getPollingFrequency());
        // don't test these - they force directory creation
        //assertEquals("efg", c.getReadFromDirectory());
        //assertEquals("fgh", c.getWriteToDirectory());
        assertEquals(false, c.isAutoDelete());
        assertEquals(true, c.isOutputAppend());
        assertEquals(true, c.isSerialiseObjects());
        assertEquals(false, c.isStreaming());

        // Not implemented yet, see MULE-2671
        //assertNull(c.getComparator());
        //assertFalse(c.isReverseOrder());

        FilenameParser parser = c.getFilenameParser();
        assertTrue(parser.getClass().getName(), c.getFilenameParser() instanceof DummyFilenameParser);

        assertTrue(c.isConnected());
        assertTrue(c.isStarted());
    }

    public void testSecondConnector() throws Exception
    {
        FileConnector c = (FileConnector)muleContext.getRegistry().lookupConnector("secondConnector");
        assertNotNull(c);

        FilenameParser parser = c.getFilenameParser();
        assertTrue(parser.getClass().getName(), c.getFilenameParser() instanceof SimpleFilenameParser);

        assertTrue(c.isConnected());
        assertTrue(c.isStarted());
    }

    public void testThirdConnector() throws Exception
    {
        FileConnector c = (FileConnector)muleContext.getRegistry().lookupConnector("thirdConnector");
        assertNotNull(c);

        FilenameParser parser = c.getFilenameParser();
        assertTrue(parser.getClass().getName(), c.getFilenameParser() instanceof ExpressionFilenameParser);

        assertTrue(c.isConnected());
        assertTrue(c.isStarted());
    }

}
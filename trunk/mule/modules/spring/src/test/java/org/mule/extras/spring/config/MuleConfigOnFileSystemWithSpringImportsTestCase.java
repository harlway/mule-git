/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.extras.spring.config;


/**
 * Tests a Mule config from the file system (i.e., not from the classpath) which loads Spring configs 
 * via <import resource="file.xml"/> statements.  The Spring configs will be loaded from the same directory
 * as the Mule config (Spring uses relative path by default).
 */
public class MuleConfigOnFileSystemWithSpringImportsTestCase extends MuleConfigWithSpringImportsTestCase
{
    public String getConfigResources()
    {
        return "./src/test/resources/mule-config-with-imports.xml";
    }
}



/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transformers.csv;

/**
 * All CSV parsers should implement this interface.
 */
public interface CSVParser
{
    public static char DEFAULT_SEPARATOR = ',';
    public static char DEFAULT_QUOTE_CHARACTER = '"';
}

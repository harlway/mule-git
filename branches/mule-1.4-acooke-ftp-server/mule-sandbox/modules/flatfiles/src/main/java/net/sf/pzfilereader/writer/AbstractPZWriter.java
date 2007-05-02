/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package net.sf.pzfilereader.writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * This class encapsulates the writer that's used to output the data.
 */
public abstract class AbstractPZWriter extends Object implements PZWriter
{
    private BufferedWriter writer;
    private Map rowMap;

    public AbstractPZWriter(OutputStream output)
    {
        super();
        writer = new BufferedWriter(new OutputStreamWriter(output));
    }
    
    public void addRecordEntry(String columnName, Object value)
    {
        if (rowMap == null)
        {
            rowMap = new HashMap();
        }
        
        if (this.validateColumnTitle(columnName) == false)
        {
            throw new IllegalArgumentException("unknown column: \"" + columnName + "\"");
        }
        rowMap.put(columnName, value);
    }
    
    /**
     * Subclasses must implement this method to perform validation of <code>columnTitle</code>.
     * 
     * @param columnTitle title of the column to be filled
     * @return <code>true</code> if the column title is valid else return <code>false</code>.
     */
    protected abstract boolean validateColumnTitle(String columnTitle);

    /**
     * Writes a newline to the output and discards the <code>rowMap</code>.
     * <p>
     * This method must be overridden by subclasses to write out the record data
     * stored in <code>rowMap</code>. Overriders <b>must</b> call <code>super.nextRecord()</code>
     * as the last call in their implementation.
     */
    public void nextRecord() throws IOException
    {
        // the row should have been written out by the subclass so it's safe to discard it here
        rowMap = null;
        writer.newLine();
    }
    
    protected void write(Object value) throws IOException
    {
        if (value == null)
        {
            value = "";
        }
        // TODO converter/formatter for converting value to string?
        writer.write(value.toString());
    }
    
    protected void write(char character) throws IOException
    {
        writer.write(character);
    }
    
    public void flush() throws IOException
    {
        writer.flush();
    }

    public void close() throws IOException
    {
        writer.flush();
        writer.close();
    }
    
    protected Map getRowMap()
    {
        return rowMap;
    }
}



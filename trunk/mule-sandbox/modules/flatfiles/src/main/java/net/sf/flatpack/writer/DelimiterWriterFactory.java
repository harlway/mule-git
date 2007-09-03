
package net.sf.flatpack.writer;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import net.sf.flatpack.structure.ColumnMetaData;
import net.sf.flatpack.util.FPConstants;

import org.jdom.JDOMException;

public class DelimiterWriterFactory extends AbstractWriterFactory
{
    public static final char DEFAULT_DELIMITER = ';';
    public static final char DEFAULT_QUALIFIER = '"';

    private final char delimiter;
    private final char qualifier;

    public DelimiterWriterFactory(char delimiter, char qualifier)
    {
        super();
        this.delimiter = delimiter;
        this.qualifier = qualifier;
    }

    public DelimiterWriterFactory(Reader mappingSrc) throws IOException, JDOMException
    {
        this(mappingSrc, DEFAULT_DELIMITER);
    }

    public DelimiterWriterFactory(Reader mappingSrc, char delimiter) throws IOException, JDOMException
    {
        this(mappingSrc, delimiter, DEFAULT_QUALIFIER);
    }

    public DelimiterWriterFactory(Reader mappingSrc, char delimiter, char qualifier) throws IOException
    {
        super(mappingSrc);
        this.delimiter = delimiter;
        this.qualifier = qualifier;
    }

    public DelimiterWriterFactory(Map mapping)
    {
        this(mapping, DEFAULT_DELIMITER, DEFAULT_QUALIFIER);
    }

    public DelimiterWriterFactory(Map mapping, char delimiter)
    {
        this(mapping, delimiter, DEFAULT_QUALIFIER);
    }

    public DelimiterWriterFactory(Map mapping, char delimiter, char qualifier)
    {
        super(mapping);
        this.delimiter = delimiter;
        this.qualifier = qualifier;
    }

    public char getDelimiter()
    {
        return delimiter;
    }

    public char getQualifier()
    {
        return qualifier;
    }

    public Writer createWriter(java.io.Writer out) throws IOException
    {
        return new DelimiterWriter(this.getColumnMapping(), out, delimiter, qualifier);
    }

    // TODO DO: check that no column titles can be added after first nextRecord
    public void addColumnTitle(String columnTitle)
    {
        Map columnMapping = this.getColumnMapping();
        List columnMetaDatas = (List) columnMapping.get(FPConstants.DETAIL_ID);
        Map columnIndices = (Map) columnMapping.get(FPConstants.COL_IDX);

        ColumnMetaData metaData = new ColumnMetaData();
        metaData.setColName(columnTitle);
        columnMetaDatas.add(metaData);

        Integer columnIndex = new Integer(columnMetaDatas.indexOf(metaData));
        columnIndices.put(columnIndex, columnTitle);
    }
}

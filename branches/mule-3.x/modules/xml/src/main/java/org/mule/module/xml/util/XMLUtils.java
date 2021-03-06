/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.xml.util;

import org.mule.RequestContext;
import org.mule.api.MuleContext;
import org.mule.api.transport.OutputHandler;
import org.mule.module.xml.stax.DelegateXMLStreamReader;
import org.mule.module.xml.stax.StaxSource;
import org.mule.module.xml.transformer.DelayedResult;
import org.mule.module.xml.transformer.XmlToDomDocument;
import org.mule.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.io.DOMReader;
import org.dom4j.io.DocumentSource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * General utility methods for working with XML.
 */
public class XMLUtils extends org.mule.util.XMLUtils
{
    public static final String TRANSFORMER_FACTORY_JDK5 = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";

    // xml parser feature names for optional XSD validation
    public static final String APACHE_XML_FEATURES_VALIDATION_SCHEMA = "http://apache.org/xml/features/validation/schema";
    public static final String APACHE_XML_FEATURES_VALIDATION_SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";

    // JAXP property for specifying external XSD location
    public static final String JAXP_PROPERTIES_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

    // JAXP properties for specifying external XSD language (as required by newer
    // JAXP implementation)
    public static final String JAXP_PROPERTIES_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    public static final String JAXP_PROPERTIES_SCHEMA_LANGUAGE_VALUE = "http://www.w3.org/2001/XMLSchema";

    /**
     * Converts a DOM to an XML string.
     */
    public static String toXml(Document dom)
    {
        return new DOMReader().read(dom).asXML();
    }

    /**
     * @return a new XSLT transformer
     * @throws TransformerConfigurationException if no TransformerFactory can be located in the
     * runtime environment.
     */
    public static Transformer getTransformer() throws TransformerConfigurationException
    {
        TransformerFactory tf;
        try
        {
            tf = TransformerFactory.newInstance();
        }
        catch (TransformerFactoryConfigurationError e)
        {
            System.setProperty("javax.xml.transform.TransformerFactory", TRANSFORMER_FACTORY_JDK5);
            tf = TransformerFactory.newInstance();
        }
        if (tf != null)
        {
            return tf.newTransformer();
        }
        else
        {
            throw new TransformerConfigurationException("Unable to instantiate a TransformerFactory");
        }
    }

    public static org.dom4j.Document toDocument(Object obj, MuleContext muleContext) throws Exception
    {
        return toDocument(obj, null, muleContext);
    }
    
    /**
     * Converts an object of unknown type to an org.dom4j.Document if possible.
     * @return null if object cannot be converted
     * @throws DocumentException if an error occurs while parsing
     */
    public static org.dom4j.Document toDocument(Object obj, String externalSchemaLocation, MuleContext muleContext) throws Exception
    {
        org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
        if (externalSchemaLocation != null)
        {
            reader.setValidation(true);
            reader.setFeature(APACHE_XML_FEATURES_VALIDATION_SCHEMA, true);
            reader.setFeature(APACHE_XML_FEATURES_VALIDATION_SCHEMA_FULL_CHECKING, true);
            
            InputStream xsdAsStream = IOUtils.getResourceAsStream(externalSchemaLocation, XMLUtils.class);
            if (xsdAsStream == null)
            {
                throw new IllegalArgumentException("Couldn't find schema at " + externalSchemaLocation);
            }
    
            // Set schema language property (must be done before the schemaSource
            // is set)
            reader.setProperty(JAXP_PROPERTIES_SCHEMA_LANGUAGE, JAXP_PROPERTIES_SCHEMA_LANGUAGE_VALUE);
    
            // Need this one to map schemaLocation to a physical location
            reader.setProperty(JAXP_PROPERTIES_SCHEMA_SOURCE, xsdAsStream);
        }


        if (obj instanceof org.dom4j.Document)
        {
            return (org.dom4j.Document) obj;
        }
        else if (obj instanceof org.w3c.dom.Document)
        {
            org.dom4j.io.DOMReader domReader = new org.dom4j.io.DOMReader();
            return domReader.read((org.w3c.dom.Document) obj);
        }
        else if (obj instanceof org.xml.sax.InputSource)
        {                
            return reader.read((org.xml.sax.InputSource) obj);
        }
        else if (obj instanceof javax.xml.transform.Source || obj instanceof javax.xml.stream.XMLStreamReader)
        {                
            // TODO Find a more direct way to do this
            XmlToDomDocument tr = new XmlToDomDocument();
            tr.setMuleContext(muleContext);
            tr.setReturnClass(org.dom4j.Document.class);
            return (org.dom4j.Document) tr.transform(obj);
        }
        else if (obj instanceof java.io.InputStream)
        {                
            return reader.read((java.io.InputStream) obj);
        }
        else if (obj instanceof String)
        {
            return reader.read(new StringReader((String) obj));
        }
        else if (obj instanceof byte[])
        {
            // TODO Handle encoding/charset somehow
            return reader.read(new StringReader(new String((byte[]) obj)));
        }
        else if (obj instanceof File)
        {                
            return reader.read((File) obj);
        }
        else
        {
            return null;
        }
    }
    /**
     * Returns an XMLStreamReader for an object of unknown type if possible.
     * @return null if no XMLStreamReader can be created for the object type
     * @throws XMLStreamException
     */
    public static javax.xml.stream.XMLStreamReader toXMLStreamReader(javax.xml.stream.XMLInputFactory factory, Object obj) throws XMLStreamException
    {
        if (obj instanceof javax.xml.stream.XMLStreamReader)
        {
            return (javax.xml.stream.XMLStreamReader) obj;
        }
        else if (obj instanceof org.mule.module.xml.stax.StaxSource)
        {
            return ((org.mule.module.xml.stax.StaxSource) obj).getXMLStreamReader();
        }
        else if (obj instanceof javax.xml.transform.Source)
        {
            return factory.createXMLStreamReader((javax.xml.transform.Source) obj);
        }
        else if (obj instanceof org.xml.sax.InputSource)
        {
            return factory.createXMLStreamReader(((org.xml.sax.InputSource) obj).getByteStream());
        }
        else if (obj instanceof org.w3c.dom.Document)
        {
            return factory.createXMLStreamReader(new javax.xml.transform.dom.DOMSource((org.w3c.dom.Document) obj));
        }
        else if (obj instanceof org.dom4j.Document)
        {
            return factory.createXMLStreamReader(new org.dom4j.io.DocumentSource((org.dom4j.Document) obj));
        }
        else if (obj instanceof java.io.InputStream)
        {
            final InputStream is = (java.io.InputStream) obj;
            
            XMLStreamReader xsr = factory.createXMLStreamReader(is);
            return new DelegateXMLStreamReader(xsr) 
            {
                @Override
                public void close() throws XMLStreamException
                {
                    super.close();
                    
                    try
                    {
                        is.close();
                    }
                    catch (IOException e)
                    {
                        throw new XMLStreamException(e);
                    }
                }
                
            };
        }
        else if (obj instanceof String)
        {
            return factory.createXMLStreamReader(new StringReader((String) obj));
        }
        else if (obj instanceof byte[])
        {
            // TODO Handle encoding/charset?
            return factory.createXMLStreamReader(new ByteArrayInputStream((byte[]) obj));
        }
        else
        {
            return null;
        }
    }
    
    /**
     * Convert our object to a Source type efficiently.
     */ 
    public static javax.xml.transform.Source toXmlSource(javax.xml.stream.XMLInputFactory xmlInputFactory, boolean useStaxSource, Object src) throws Exception
    {
        if (src instanceof javax.xml.transform.Source)
        {
            return (Source) src;
        }
        else if (src instanceof byte[])
        {
            ByteArrayInputStream stream = new ByteArrayInputStream((byte[]) src);
            return toStreamSource(xmlInputFactory, useStaxSource, stream);
        }
        else if (src instanceof InputStream)
        {
            return toStreamSource(xmlInputFactory, useStaxSource, (InputStream) src);
        }
        else if (src instanceof String)
        {
            if (useStaxSource)
            {
                return new StaxSource(xmlInputFactory.createXMLStreamReader(new StringReader((String) src)));
            }
            else
            {
                return new StreamSource(new StringReader((String) src));
            }
        }
        else if (src instanceof org.dom4j.Document)
        {
            return new DocumentSource((org.dom4j.Document) src);
        }
        else if (src instanceof org.xml.sax.InputSource)
        {
            return new SAXSource((InputSource) src);
        }
        // TODO MULE-3555
        else if (src instanceof XMLStreamReader)
        {
            XMLStreamReader xsr = (XMLStreamReader) src;
            
            // StaxSource requires that we advance to a start element/document event
            if (!xsr.isStartElement() && 
                            xsr.getEventType() != XMLStreamConstants.START_DOCUMENT) 
            {
                xsr.nextTag();
            }
            
            return new StaxSource((XMLStreamReader) src);
        }
        else if (src instanceof org.w3c.dom.Document || src instanceof org.w3c.dom.Element)
        {
            return new DOMSource((org.w3c.dom.Node) src);
        }
        else if (src instanceof DelayedResult) 
        {
            DelayedResult result = ((DelayedResult) src);
            DOMResult domResult = new DOMResult();
            result.write(domResult);
            return new DOMSource(domResult.getNode());
        }
        else if (src instanceof OutputHandler) 
        {
            OutputHandler handler = ((OutputHandler) src);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            
            handler.write(RequestContext.getEvent(), output);
            
            return toStreamSource(xmlInputFactory, useStaxSource, new ByteArrayInputStream(output.toByteArray()));
        }
        else
        {
            return null;
        }
    }

    public static javax.xml.transform.Source toStreamSource(javax.xml.stream.XMLInputFactory xmlInputFactory, boolean useStaxSource, InputStream stream) throws XMLStreamException
    {
        if (useStaxSource)
        {
            return new org.mule.module.xml.stax.StaxSource(xmlInputFactory.createXMLStreamReader(stream));
        }
        else 
        {
            return new javax.xml.transform.stream.StreamSource(stream);
        }
    }
    
    /**
     * Copies the reader to the writer. The start and end document methods must
     * be handled on the writer manually. TODO: if the namespace on the reader
     * has been declared previously to where we are in the stream, this probably
     * won't work.
     * 
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    public static void copy(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        copy(reader, writer, false);
    }
    public static void copy(XMLStreamReader reader, XMLStreamWriter writer,
                            boolean fragment) throws XMLStreamException {
        // number of elements read in
        int read = 0;
        int event = reader.getEventType();

        while (reader.hasNext()) {
            switch (event) {
            case XMLStreamConstants.START_ELEMENT:
                read++;
                writeStartElement(reader, writer);
                break;
            case XMLStreamConstants.END_ELEMENT:
                writer.writeEndElement();
                read--;
                if (read <= 0 && !fragment) {
                    return;
                }
                break;
            case XMLStreamConstants.CHARACTERS:
                writer.writeCharacters(reader.getText());
                break;
            case XMLStreamConstants.START_DOCUMENT:
            case XMLStreamConstants.END_DOCUMENT:
            case XMLStreamConstants.ATTRIBUTE:
            case XMLStreamConstants.NAMESPACE:
                break;
            default:
                break;
            }
            event = reader.next();
        }
    }

    private static void writeStartElement(XMLStreamReader reader, XMLStreamWriter writer)
        throws XMLStreamException {
        String local = reader.getLocalName();
        String uri = reader.getNamespaceURI();
        String prefix = reader.getPrefix();
        if (prefix == null) {
            prefix = "";
        }

        
//        System.out.println("STAXUTILS:writeStartElement : node name : " + local +  " namespace URI" + uri);
        boolean writeElementNS = false;
        if (uri != null) {
            String boundPrefix = writer.getPrefix(uri);
            if (boundPrefix == null || !prefix.equals(boundPrefix)) {
                writeElementNS = true;
            }
        }

        // Write out the element name
        if (uri != null) {
            if (prefix.length() == 0 && StringUtils.isEmpty(uri)) {
                writer.writeStartElement(local);
                writer.setDefaultNamespace(uri);

            } else {
                writer.writeStartElement(prefix, local, uri);
                writer.setPrefix(prefix, uri);
            }
        } else {
            writer.writeStartElement(local);
        }

        // Write out the namespaces
        for (int i = 0; i < reader.getNamespaceCount(); i++) {
            String nsURI = reader.getNamespaceURI(i);
            String nsPrefix = reader.getNamespacePrefix(i);
            if (nsPrefix == null) {
                nsPrefix = "";
            }

            if (nsPrefix.length() == 0) {
                writer.writeDefaultNamespace(nsURI);
            } else {
                writer.writeNamespace(nsPrefix, nsURI);
            }

            if (nsURI.equals(uri) && nsPrefix.equals(prefix)) {
                writeElementNS = false;
            }
        }

        // Check if the namespace still needs to be written.
        // We need this check because namespace writing works
        // different on Woodstox and the RI.
        if (writeElementNS) {
            if (prefix == null || prefix.length() == 0) {
                writer.writeDefaultNamespace(uri);
            } else {
                writer.writeNamespace(prefix, uri);
            }
        }        
        
        // Write out attributes
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            String ns = reader.getAttributeNamespace(i);
            String nsPrefix = reader.getAttributePrefix(i);
            if (ns == null || ns.length() == 0) {
                writer.writeAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
            } else if (nsPrefix == null || nsPrefix.length() == 0) {
                writer.writeAttribute(reader.getAttributeNamespace(i), reader.getAttributeLocalName(i),
                                      reader.getAttributeValue(i));
            } else {
                writer.writeAttribute(reader.getAttributePrefix(i), reader.getAttributeNamespace(i), reader
                    .getAttributeLocalName(i), reader.getAttributeValue(i));
            }

        }
    }
}

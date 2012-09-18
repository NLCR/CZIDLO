/*
 * Copyright (C) 2011, 2012 Martin Řehánek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.nkp.urnnbn.xml.commons;

import java.io.*;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import nu.xom.*;
import nu.xom.xslt.XSLException;
import nu.xom.xslt.XSLTransform;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * TODO: poresit, jestli je to dobre s tema vyjimkama (validity vs parsing)
 * TODO: unit tests
 *
 * @author Martin Řehánek
 */
public class XOMUtils {

    static class MyErrorHandler implements ErrorHandler {

        @Override
        public void warning(SAXParseException exception) throws SAXException {
            String message = buildMessage(exception);
            throw new SAXException("Warning: " + message);
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            String message = buildMessage(exception);
            throw new SAXException("Error: " + message);
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            String message = buildMessage(exception);
            throw new SAXException("Fatal Error: " + message);
        }

        private String buildMessage(SAXParseException exception) {
            StringBuilder message = new StringBuilder();
            message.append("line ").append(exception.getLineNumber());
            // message.append(", URI: ").append(exception.getSystemId());
            message.append(": ").append(exception.getMessage()).append('\n');
            return message.toString();
        }
    }

    /**
     *
     * @param xml File containing xml to be validated and returned
     * @param schema xsd file
     * @return valid document
     * @throws ValidityException
     * @throws ParsingException
     * @throws IOException
     */
    public static Document loadDocumentValidByExternalXsd(File xml, File schema) throws ValidityException, ParsingException, IOException {
        return new Builder(readerValidatingByXsdFromFile(schema)).build(xml);
    }

    /**
     *
     * @param xml String containing xml to be validated and returned
     * @param schema xsd file
     * @return valid document
     * @throws ValidityException
     * @throws ParsingException
     * @throws IOException
     */
    public static Document loadDocumentValidByExternalXsd(String xml, File schema) throws ValidityException, ParsingException, IOException {
        return new Builder(readerValidatingByXsdFromFile(schema)).build(xml, null);
    }

    /**
     *
     * @param xml File containing xml to be validated and returned
     * @param schemaUrl url of the schema
     * @return valid document
     * @throws ParsingException
     * @throws ValidityException
     * @throws IOException
     */
    public static Document loadDocumentValidByExternalXsd(File xml, URL schemaUrl) throws ParsingException, ValidityException, IOException {
        return new Builder(readerValidatingByXsdFromUrl(schemaUrl)).build(xml);
    }

    /**
     *
     * @param xml String containing xml to be validated and returned
     * @param schemaUrl url of the schema
     * @return valid document
     * @throws ParsingException
     * @throws ValidityException
     * @throws IOException
     */
    public static Document loadDocumentValidByExternalXsd(String xml, URL schemaUrl) throws ParsingException, ValidityException, IOException {
        return new Builder(readerValidatingByXsdFromUrl(schemaUrl)).build(xml, null);
    }

    /**
     *
     * @param xml String containing xml to be validated and returned
     * @param schema String containing xsd
     * @return valid document
     * @throws ParsingException
     * @throws ValidityException
     * @throws IOException
     */
    public static Document loadDocumentValidByExternalXsd(String xml, String schema) throws ParsingException, ValidityException, IOException {
        return new Builder(readerValidatingByXsdFromString(schema)).build(xml, null);
    }

    /**
     *
     * @param xml File containing xml to be validated and returned
     * @return valid document
     * @throws ParsingException
     * @throws ValidityException
     * @throws IOException
     */
    public static Document loadDocumentValidByInternalXsd(File xml) throws ParsingException, ValidityException, IOException {
        return new Builder(readerValidatingByInternalXsd()).build(xml);
    }

    /**
     *
     * @param xml String containing xml to be validated and returned
     * @return valid document
     * @throws ParsingException
     * @throws ValidityException
     * @throws IOException
     */
    public static Document loadDocumentValidByInternalXsd(String xml) throws ParsingException, ValidityException, IOException {
        return new Builder(readerValidatingByInternalXsd()).build(xml, null);
    }

    /**
     *
     * @param xml InputStream that document can be read from
     * @return valid document
     * @throws ParsingException
     * @throws ValidityException
     * @throws IOException
     */
    public static Document loadDocumentValidByInternalXsd(InputStream xml) throws ParsingException, ValidityException, IOException {
        return new Builder(readerValidatingByInternalXsd()).build(xml);
    }

    /**
     *
     * @param xml File containing document to be read
     * @return
     * @throws ParsingException
     * @throws IOException
     */
    public static Document loadDocumentWithoutValidation(File xml) throws ParsingException, IOException {
        return new Builder().build(xml);
    }

    /**
     *
     * @param xml String containing document to be read
     * @return
     * @throws ParsingException
     * @throws IOException
     */
    public static Document loadDocumentWithoutValidation(String xml) throws ParsingException, IOException {
        return new Builder().build(xml, null);
    }

    /**
     *
     * @param in InputStream that document can be read from
     * @return
     * @throws ParsingException
     * @throws IOException
     */
    public static Document loadDocumentWithoutValidation(InputStream in) throws ParsingException, IOException {
        return new Builder().build(in);
    }

    private static XMLReader readerValidatingByXsdFromString(String schema) throws ValidityException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            factory.setSchema(schemaFactory.newSchema(new Source[]{new StreamSource(new StringReader(schema))}));
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setErrorHandler(new MyErrorHandler());
            return reader;
        } catch (ParserConfigurationException ex) {
            throw new ValidityException(ex.getMessage(), ex);
        } catch (SAXException ex) {

            throw new ValidityException(ex.getMessage(), ex);
        }
    }

    private static XMLReader readerValidatingByXsdFromFile(File schema) throws ValidityException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            factory.setSchema(schemaFactory.newSchema(new Source[]{new StreamSource(schema)}));
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setErrorHandler(new MyErrorHandler());
            return reader;
        } catch (ParserConfigurationException ex) {
            throw new ValidityException(ex.getMessage(), ex);
        } catch (SAXException ex) {
            throw new ValidityException(ex.getMessage(), ex);
        }
    }

    private static XMLReader readerValidatingByXsdFromUrl(URL url) throws ValidityException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            InputStream in = null;
            try {
                in = url.openStream();
                factory.setSchema(schemaFactory.newSchema(new Source[]{new StreamSource(in)}));
                SAXParser parser = factory.newSAXParser();
                XMLReader reader = parser.getXMLReader();
                reader.setErrorHandler(new MyErrorHandler());
                return reader;
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (IOException ex) {
            throw new ValidityException(ex.getMessage(), ex);
        } catch (ParserConfigurationException ex) {
            throw new ValidityException(ex.getMessage(), ex);
        } catch (SAXException ex) {
            throw new ValidityException(ex.getMessage(), ex);
        }
    }

    private static XMLReader readerValidatingByInternalXsd() throws ValidityException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(true);
            SAXParser parser = factory.newSAXParser();
            parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
            XMLReader reader = parser.getXMLReader();
            reader.setErrorHandler(new MyErrorHandler());
            return reader;
        } catch (ParserConfigurationException ex) {
            throw new ValidityException(ex.getMessage(), ex);
        } catch (SAXException ex) {
            throw new ValidityException(ex.getMessage(), ex);
        }
    }

    public static Document transformDocument(String inputXml, String xslt) throws ParsingException, ValidityException, IOException, XMLException, XSLException {
        Builder builder = new Builder();
        Document input = builder.build(inputXml, null);
        Document stylesheet = builder.build(xslt, null);
        return transformDocument(input, stylesheet);
    }

    public static Document transformDocument(Document input, Document xslt) throws XMLException, XSLException {
        XSLTransform transform = new XSLTransform(xslt);
        Nodes output = transform.transform(input);
        return XSLTransform.toDocument(output);
    }

    public static void saveDocumentToFile(Document doc, File file) throws FileNotFoundException, IOException {
        OutputStream out = new FileOutputStream(file);
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.setIndent(4);
        serializer.setMaxLength(64);
        serializer.write(doc);
    }
}

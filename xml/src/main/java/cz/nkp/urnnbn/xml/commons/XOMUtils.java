/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.commons;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 *
 * @author Martin Řehánek
 */
public class XOMUtils {

    static class MyErrorHandler implements ErrorHandler {

        public void warning(SAXParseException exception) throws SAXException {
            String message = buildMessage(exception);
            throw new SAXException("Warning: " + message);
        }

        public void error(SAXParseException exception) throws SAXException {
            String message = buildMessage(exception);
            throw new SAXException("Error: " + message);
        }

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

    public static Document loadDocumentValidByExternalXsd(File xml, File schema) throws ValidityException, ParsingException, IOException {
        return new Builder(readerValidatinByXsdFromFile(schema)).build(xml);
    }

    public static Document loadDocumentValidByExternalXsd(String xml, File schema) throws ValidityException, ParsingException, IOException {
        return new Builder(readerValidatinByXsdFromFile(schema)).build(xml, null);
    }

    public static Document loadDocumentValidByExternalXsd(File xml, URL schemaUrl) throws ParsingException, ValidityException, IOException {
        return new Builder(readerValidatingByXsdFromUrl(schemaUrl)).build(xml);
    }

    public static Document loadDocumentValidByExternalXsd(String xml, URL schemaUrl) throws ParsingException, ValidityException, IOException {
        return new Builder(readerValidatingByXsdFromUrl(schemaUrl)).build(xml, null);
    }

    public static Document loadDocumentValidByInternalXsd(File xml) throws ParsingException, ValidityException, IOException {
        return new Builder(readerValidatingByInternalXsd()).build(xml);
    }

    public static Document loadDocumentValidByInternalXsd(String xml) throws ParsingException, ValidityException, IOException {
        return new Builder(readerValidatingByInternalXsd()).build(xml, null);
    }

    public static Document loadDocumentWithoutValidation(File xml) throws ParsingException, IOException {
        return new Builder().build(xml);
    }

    public static Document loadDocumentWithoutValidation(String xml) throws ParsingException, IOException {
        return new Builder().build(xml, null);
    }

    private static XMLReader readerValidatinByXsdFromFile(File schema) throws ValidityException {
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
}

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
import nu.xom.*;
import nu.xom.xslt.XSLException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * TODO: poresit, jestli je to dobre s tema vyjimkama (validity vs parsing) TODO: unit tests
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
     * @param xmlFile
     *            File containing xml to be validated and returned
     * @param xsdFile
     *            xsd file
     * @return valid document
     * @throws ValidityException
     * @throws ParsingException
     * @throws IOException
     */
    public static Document loadDocumentValidByExternalXsd(File xmlFile, File xsdFile) throws ValidityException, ParsingException, IOException {
        return new ExternalXsdValitatingXmlLoader(xsdFile).loadDocument(xmlFile);
    }

    /**
     *
     * @param xmlString
     *            String containing xml to be validated and returned
     * @param xsdFile
     *            xsd file
     * @return valid document
     * @throws ValidityException
     * @throws ParsingException
     * @throws IOException
     */
    public static Document loadDocumentValidByExternalXsd(String xmlString, File xsdFile) throws ValidityException, ParsingException, IOException {
        return new ExternalXsdValitatingXmlLoader(xsdFile).loadDocument(xmlString);
    }

    /**
     *
     * @param xmlFile
     *            File containing xml to be validated and returned
     * @param xsdUrl
     *            url of the schema
     * @return valid document
     * @throws ParsingException
     * @throws ValidityException
     * @throws IOException
     */
    public static Document loadDocumentValidByExternalXsd(File xmlFile, URL xsdUrl) throws ParsingException, ValidityException, IOException {
        return new ExternalXsdValitatingXmlLoader(xsdUrl).loadDocument(xmlFile);
    }

    /**
     *
     * @param xmlString
     *            String containing xml to be validated and returned
     * @param xsdUrl
     *            url of the schema
     * @return valid document
     * @throws ParsingException
     * @throws ValidityException
     * @throws IOException
     */
    public static Document loadDocumentValidByExternalXsd(String xmlString, URL xsdUrl) throws ParsingException, ValidityException, IOException {
        return new ExternalXsdValitatingXmlLoader(xsdUrl).loadDocument(xmlString);
    }

    /**
     *
     * @param xmlString
     *            String containing xml to be validated and returned
     * @param xsdString
     *            String containing xsd
     * @return valid document
     * @throws ParsingException
     * @throws ValidityException
     * @throws IOException
     */
    public static Document loadDocumentValidByExternalXsd(String xmlString, String xsdString) throws ParsingException, ValidityException, IOException {
        return new ExternalXsdValitatingXmlLoader(xsdString).loadDocument(xmlString);
    }

    /**
     *
     * @param xmlFile
     *            File containing xml to be validated and returned
     * @return valid document
     * @throws ParsingException
     * @throws ValidityException
     * @throws IOException
     */
    public static Document loadDocumentValidByInternalXsd(File xmlFile) throws ParsingException, ValidityException, IOException {
        return new InternalXsdValidatingXmlLoader().loadDocument(xmlFile);
    }

    /**
     *
     * @param xml
     *            String containing xml to be validated and returned
     * @return valid document
     * @throws ParsingException
     * @throws ValidityException
     * @throws IOException
     */
    public static Document loadDocumentValidByInternalXsd(String xml) throws ParsingException, ValidityException, IOException {
        return new InternalXsdValidatingXmlLoader().loadDocument(xml);
    }

    /**
     *
     * @param xml
     *            InputStream that document can be read from
     * @return valid document
     * @throws ParsingException
     * @throws ValidityException
     * @throws IOException
     */
    public static Document loadDocumentValidByInternalXsd(InputStream xml) throws ParsingException, ValidityException, IOException {
        return new InternalXsdValidatingXmlLoader().loadDocument(xml);
    }

    /**
     *
     * @param xmlFile
     *            File containing document to be read
     * @return
     * @throws ParsingException
     * @throws IOException
     */
    public static Document loadDocumentWithoutValidation(File xmlFile) throws ParsingException, IOException {
        return new NonvalidatingXmlLoader().loadDocument(xmlFile);
    }

    /**
     *
     * @param xmlString
     *            String containing document to be read
     * @return
     * @throws ParsingException
     * @throws IOException
     */
    public static Document loadDocumentWithoutValidation(String xmlString) throws ParsingException, IOException {
        return new NonvalidatingXmlLoader().loadDocument(xmlString);
    }

    /**
     *
     * @param xmlStream
     *            InputStream that document can be read from
     * @return
     * @throws ParsingException
     * @throws IOException
     */
    public static Document loadDocumentWithoutValidation(InputStream xmlStream) throws ParsingException, IOException {
        return new NonvalidatingXmlLoader().loadDocument(xmlStream);
    }

    public static Document transformDocument(String inputXml, String xslt) throws ParsingException, ValidityException, IOException, XMLException,
            XSLException {
        XsltXmlTransformer transformer = new XsltXmlTransformer(xslt);
        return transformer.transform(inputXml);
    }

    public static Document transformDocument(Document inputDoc, Document xslt) throws XMLException, XSLException {
        XsltXmlTransformer transformer = new XsltXmlTransformer(xslt);
        return transformer.transform(inputDoc);
    }

    public static void saveDocumentToFile(Document doc, File file) throws FileNotFoundException, IOException {
        OutputStream out = new FileOutputStream(file);
        Serializer serializer = new Serializer(out, "UTF-8");
        serializer.setIndent(4);
        serializer.setMaxLength(64);
        serializer.write(doc);
    }
}

/*
 * Copyright (C) 2013 Martin Řehánek
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import nu.xom.ValidityException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 *
 * @author Martin Řehánek
 */
public class ExternalXsdValitatingXmlLoader extends ValidatingXmlLoader {

    public ExternalXsdValitatingXmlLoader(File xsdFile) throws ValidityException {
        super(readerValidatingByXsdFromFile(xsdFile));
    }

    public ExternalXsdValitatingXmlLoader(URL xsdUrl) throws ValidityException {
        super(readerValidatingByXsdFromUrl(xsdUrl));
    }

    public ExternalXsdValitatingXmlLoader(InputStream xsdStream) throws ValidityException {
        super(readerValidatingByXsdFromInputStream(xsdStream));
    }
    
    public ExternalXsdValitatingXmlLoader(String xsdDoc) throws ValidityException {
        super(readerValidatingByXsdFromString(xsdDoc));
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
            reader.setErrorHandler(new XOMUtils.MyErrorHandler());
            return reader;
        } catch (ParserConfigurationException ex) {
            throw new ValidityException(ex.getMessage(), ex);
        } catch (SAXException ex) {
            throw new ValidityException(ex.getMessage(), ex);
        }
    }

    private static XMLReader readerValidatingByXsdFromUrl(URL url) throws ValidityException {
        try {
            return readerValidatingByXsdFromInputStream(url.openStream());
        } catch (IOException ex) {
            throw new ValidityException(ex.getMessage(), ex);
        }
    }

    private static XMLReader readerValidatingByXsdFromInputStream(InputStream in) throws ValidityException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            factory.setSchema(schemaFactory.newSchema(new Source[]{new StreamSource(in)}));
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setErrorHandler(new XOMUtils.MyErrorHandler());
            return reader;
        } catch (ParserConfigurationException ex) {
            throw new ValidityException(ex.getMessage(), ex);
        } catch (SAXException ex) {
            throw new ValidityException(ex.getMessage(), ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                throw new ValidityException(ex.getMessage(), ex);
            }
        }
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
            reader.setErrorHandler(new XOMUtils.MyErrorHandler());
            return reader;
        } catch (ParserConfigurationException ex) {
            throw new ValidityException(ex.getMessage(), ex);
        } catch (SAXException ex) {
            throw new ValidityException(ex.getMessage(), ex);
        }
    }
}

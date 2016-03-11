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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import nu.xom.ValidityException;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 *
 * @author Martin Řehánek
 */
public class InternalXsdValidatingXmlLoader extends ValidatingXmlLoader {

    public InternalXsdValidatingXmlLoader() throws ValidityException {
        super(readerValidatingByInternalXsd());
    }

    private static XMLReader readerValidatingByInternalXsd() throws ValidityException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(true);
            SAXParser parser = factory.newSAXParser();
            parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
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

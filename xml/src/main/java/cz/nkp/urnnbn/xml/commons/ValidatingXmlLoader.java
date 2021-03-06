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

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.xml.sax.XMLReader;

/**
 *
 * @author Martin Řehánek
 */
public abstract class ValidatingXmlLoader {

    protected final XMLReader reader;

    public ValidatingXmlLoader(XMLReader reader) {
        this.reader = reader;
    }

    protected final Builder builder() {
        return new Builder(reader);
    }

    public Document loadDocument(InputStream xmlStream) throws ParsingException, IOException, ValidityException {
        return builder().build(xmlStream);
    }

    public Document loadDocument(String xmlString) throws ParsingException, IOException, ValidityException {
        return builder().build(xmlString, null);
    }

    public Document loadDocument(File xmlFile) throws ParsingException, IOException, ValidityException {
        return builder().build(xmlFile);
    }
}

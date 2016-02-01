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
package cz.nkp.urnnbn.xml.examples;

import cz.nkp.urnnbn.xml.commons.XOMUtils;
import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;
import nu.xom.Document;
import nu.xom.ParsingException;

/**
 * TODO: remove absolute paths TODO: move to module api
 *
 * @author Martin Řehánek
 */
public class Identifiers extends TestCase {

    File xmlDir = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xml");
    File identifiersSchemaFile = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xsd/identifiers.xsd.xml");
    String identifiersSchema;

    public Identifiers(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        identifiersSchema = XOMUtils.loadDocumentWithoutValidation(identifiersSchemaFile).toXML();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testValidateIdentifiers() throws Exception {
        String xmlString = toXmlString(xmlDir, "identifiers.xml");
        System.out.println("loading " + xmlString);
        Document doc = XOMUtils.loadDocumentValidByExternalXsd(xmlString, identifiersSchema);
        assertNotNull(doc);
    }

    private String toXmlString(File rootDir, String filename) throws ParsingException, IOException {
        File file = new File(rootDir.getAbsolutePath() + File.separator + filename);
        return XOMUtils.loadDocumentWithoutValidation(file).toXML();
    }
}

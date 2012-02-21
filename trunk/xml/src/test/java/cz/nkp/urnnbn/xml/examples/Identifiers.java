/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.examples;

import cz.nkp.urnnbn.xml.commons.XOMUtils;
import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;
import nu.xom.ParsingException;

/**
 *
 * @author Martin Řehánek
 */
public class Identifiers extends TestCase {

    File xmlDir = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xml/request/importRecord");
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
        XOMUtils.loadDocumentValidByExternalXsd(xmlString, identifiersSchema);
    }

    private String toXmlString(File rootDir, String filename) throws ParsingException, IOException {
        File file = new File(rootDir.getAbsolutePath() + File.separator + filename);
        return XOMUtils.loadDocumentWithoutValidation(file).toXML();
    }
}

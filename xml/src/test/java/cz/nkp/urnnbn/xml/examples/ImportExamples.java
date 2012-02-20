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
public class ImportExamples extends TestCase {

    File importExamplesDir = new File("/home/martin/NetBeansProjects/xml/src/main/java/cz/nkp/urnnbn/xml/examples/request");
    File importSchemaFile = new File("/home/martin/NetBeansProjects/xml/src/main/java/cz/nkp/urnnbn/xml/xsd/importRecord.xsd.xml");
    String importSchema;

    public ImportExamples(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        importSchema = XOMUtils.loadDocumentWithoutValidation(importSchemaFile).toXML();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testValidateImportMonograph() throws Exception {
        String xmlString = toXmlString(importExamplesDir, "import-monograph.xml");
        XOMUtils.loadDocumentValidByExternalXsd(xmlString, importSchema);
    }

    public void testValidateImportMonographVolume() throws Exception {
        String xmlString = toXmlString(importExamplesDir, "import-monographVolume.xml");
        XOMUtils.loadDocumentValidByExternalXsd(xmlString, importSchema);
    }

    public void testValidateImportPeriodical() throws Exception {
        String xmlString = toXmlString(importExamplesDir, "import-periodical.xml");
        XOMUtils.loadDocumentValidByExternalXsd(xmlString, importSchema);
    }

    public void testValidateImportPeriodicalVolume() throws Exception {
        String xmlString = toXmlString(importExamplesDir, "import-periodicalVolume.xml");
        XOMUtils.loadDocumentValidByExternalXsd(xmlString, importSchema);
    }

    public void testValidateImportPeriodicalIssue() throws Exception {
        String xmlString = toXmlString(importExamplesDir, "import-periodicalIssue.xml");
        XOMUtils.loadDocumentValidByExternalXsd(xmlString, importSchema);
    }

    public void testValidateImportThesis() throws Exception {
        String xmlString = toXmlString(importExamplesDir, "import-thesis.xml");
        XOMUtils.loadDocumentValidByExternalXsd(xmlString, importSchema);
    }

    public void testValidateImportAnalytical() throws Exception {
        String xmlString = toXmlString(importExamplesDir, "import-analytical.xml");
        XOMUtils.loadDocumentValidByExternalXsd(xmlString, importSchema);
    }

    public void testValidateImportOtherEntity() throws Exception {
        String xmlString = toXmlString(importExamplesDir, "import-otherEntity.xml");
        XOMUtils.loadDocumentValidByExternalXsd(xmlString, importSchema);
    }

    private String toXmlString(File rootDir, String filename) throws ParsingException, IOException {
        File file = new File(rootDir.getAbsolutePath() + File.separator + filename);
        return XOMUtils.loadDocumentWithoutValidation(file).toXML();
    }
}

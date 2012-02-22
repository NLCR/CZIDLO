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

    File importExamplesDir = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xml/request/importRecord");
    File importSchemaFile = new File("/home/martin/NetBeansProjects/xml/src/main/resources/xsd/recordImport.xsd.xml");
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

    public void testValidateMonographImport() throws Exception {
        String xmlString = toXmlString(importExamplesDir, "monograph.xml");
        XOMUtils.loadDocumentValidByExternalXsd(xmlString, importSchema);
    }

    public void testValidateMonographWithUrnImport() throws Exception {
        String xmlString = toXmlString(importExamplesDir, "monograph-withUrnNbn.xml");
        XOMUtils.loadDocumentValidByExternalXsd(xmlString, importSchema);
    }

    public void testValidateMonographVolumeImport() throws Exception {
        String xmlString = toXmlString(importExamplesDir, "monographVolume.xml");
        XOMUtils.loadDocumentValidByExternalXsd(xmlString, importSchema);
    }

    public void testValidatePeriodicalImport() throws Exception {
        String xmlString = toXmlString(importExamplesDir, "periodical.xml");
        XOMUtils.loadDocumentValidByExternalXsd(xmlString, importSchema);
    }

    public void testValidatePeriodicalVolumeImport() throws Exception {
        String xmlString = toXmlString(importExamplesDir, "periodicalVolume.xml");
        XOMUtils.loadDocumentValidByExternalXsd(xmlString, importSchema);
    }

    public void testValidatePeriodicalIssueImport() throws Exception {
        String xmlString = toXmlString(importExamplesDir, "periodicalIssue.xml");
        XOMUtils.loadDocumentValidByExternalXsd(xmlString, importSchema);
    }

    public void testValidateThesisImport() throws Exception {
        String xmlString = toXmlString(importExamplesDir, "thesis.xml");
        XOMUtils.loadDocumentValidByExternalXsd(xmlString, importSchema);
    }

    public void testValidateAnalyticalImport() throws Exception {
        String xmlString = toXmlString(importExamplesDir, "analytical.xml");
        XOMUtils.loadDocumentValidByExternalXsd(xmlString, importSchema);
    }

    public void testValidateOtherEntity_mapImport() throws Exception {
        String xmlString = toXmlString(importExamplesDir, "otherEntity-map.xml");
        XOMUtils.loadDocumentValidByExternalXsd(xmlString, importSchema);
    }

    public void testValidateOtherEntity_musicSheetImport() throws Exception {
        String xmlString = toXmlString(importExamplesDir, "otherEntity-musicSheet.xml");
        XOMUtils.loadDocumentValidByExternalXsd(xmlString, importSchema);
    }

    private String toXmlString(File rootDir, String filename) throws ParsingException, IOException {
        File file = new File(rootDir.getAbsolutePath() + File.separator + filename);
        return XOMUtils.loadDocumentWithoutValidation(file).toXML();
    }
}

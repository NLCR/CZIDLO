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

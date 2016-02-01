/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.tools;

import cz.nkp.urnnbn.oaipmhprovider.repository.MetadataFormat;
import cz.nkp.urnnbn.oaipmhprovider.repository.OaiSet;
import cz.nkp.urnnbn.oaipmhprovider.repository.PresentRecord;
import cz.nkp.urnnbn.oaipmhprovider.repository.Record;
import cz.nkp.urnnbn.oaipmhprovider.tools.dom4j.Dom4jUtils;
import java.io.IOException;
import org.dom4j.Attribute;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.XPath;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek (rehan at mzk.cz)
 */
public class ElementAppender {

    static Namespace xsi = DocumentHelper.createNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    static Namespace xsi2 = DocumentHelper.createNamespace("xsi2", "http://www.w3.org/2001/XMLSchema-instance");
    static final String MARC_ALEPH_SCHEMA_LOCATION = "http://www.loc.gov/MARC21/slim http://iris.mzk.cz/cache/marcxml_aleph.xsd";
    static XPath metadataRootElPath = Dom4jUtils.createXPath("./*");

    public static void appendHeaderType(Element rootEl, Record record) {
        Element headerEl = rootEl.addElement("header");
        if (record.isDeleted()) {
            headerEl.addAttribute("status", "deleted");
        }
        Element identifierEl = headerEl.addElement("identifier");
        // identifierEl.addText(record.getId().toString());
        identifierEl.addText(record.getId().toString());

        Element datestampEl = headerEl.addElement("datestamp");
        datestampEl.addText(record.getDateStamp().toString());
        appendSetSpecType(headerEl, record);
    }

    public static void appendSetSpecType(Element headerEl, Record record) {
        for (OaiSet set : record.getOaiSets()) {
            Element setSpecEl = headerEl.addElement("setSpec");
            setSpecEl.addText(set.getSetSpec());
        }
    }

    public static void appendResumptionToken(Element rootEl, int completeSize, int cursor, DateTime validUntil, String token) {
        Element resumptionTokenEl = rootEl.addElement("resumptionToken");
        resumptionTokenEl.addAttribute("completeListSize", String.valueOf(completeSize));
        resumptionTokenEl.addAttribute("cursor", String.valueOf(cursor));
        if (token != null) {
            resumptionTokenEl.addAttribute("expirationDate", validUntil.toString());
            resumptionTokenEl.addText(token);
        }
    }

    public static void appendRecord(Element rootEl, Record record) throws IOException {
        Element recordEl = rootEl.addElement("record");
        appendHeaderType(recordEl, record);
        if (!record.isDeleted()) {
            appendMetadata(recordEl, (PresentRecord) record);
        }
    }

    private static void appendMetadata(Element recordEl, PresentRecord record) throws IOException {
        Element metadataEl = recordEl.addElement("metadata");
        if (record == null) {
            System.err.println("record==null");
        } else if (record.getMetadata() == null) {
            System.err.println("record.getMetadata() == null");
        } else if (record.getMetadata().getRootElement() == null) {
            System.err.println("record.getMetadata().getRootElement() == null");
        }
        metadataEl.add(record.getMetadata().getRootElement().detach());
        Element metadataRootEl = (Element) metadataRootElPath.selectSingleNode(metadataEl);
        if (metadataRootEl != null) {
            String schemaLocation = getAndDetachSchemaLocation(metadataRootEl, record.getMetadataFormat());
            // add xsi redefinition
            metadataRootEl.add(xsi2);
            // add schema location
            if (schemaLocation != null) {
                metadataRootEl.addAttribute(new QName("schemaLocation", xsi2), schemaLocation);
            }
        }
    }

    private static String getAndDetachSchemaLocation(Element metadataRootEl, MetadataFormat metadataFormat) {
        Attribute schemaAttr = metadataRootEl.attribute(new QName("schemaLocation", xsi));
        if (schemaAttr == null) {
            return null;
        } else {
            // TODO: povolit, az se ozve clovek z EOD
            // take schema presunout z iris na oai.mzk.cz
            // String result = metadataFormat == MetadataFormat.marc21
            // ? MARC_ALEPH_SCHEMA_LOCATION
            // : schemaAttr.getText();
            String result = schemaAttr.getText();
            schemaAttr.detach();// bez toho by se nove schemLocation namapovalo na xsi a ne xsi2
            return result;
        }
    }
}

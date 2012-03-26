/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.legacyrecordsimport;

import cz.nkp.urnnbn.xml.commons.Namespaces;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

/**
 *
 * @author Martin Řehánek
 */
public class RecordImportBuilder {

    static String RESOLVER = Namespaces.RESOLVER;
    private static String NAMESPACE_PREFIX = "r";
    private static String SPACE = " ";
    private static String TWO_SPACES = "  ";
    private static String URNNBN_PREFIX = "urn:nbn:cz:";
    private final Connection con;
    private final File resultDir;
    private final File datastampDir;
    

    public RecordImportBuilder(Connection con, File resultDir, File datastampDir) {
        this.con = con;
        this.resultDir = resultDir;
        this.datastampDir = datastampDir;
    }

    void buildFiles() {
        try {
            Statement st = con.createStatement();
            String statement = "select  r.UPDATE_TIMESTAMP as DD_updated,r.CISLO_RDCZ, r.URNNBN,"
                    + "r.PRIDELENO_DNE as URN_prideleno, r.FORMAT, r.DOSTUPNOST, r.SIGLA,"
                    + "r.CISLO_ZAKAZKY, r.FINANCOVANO, e.UPDATE_TIMESTAMP as IE_updated,"
                    + "e.CCNB, e.ISSN, e.DRUH_DOKUMENTU, e.NAZEV, e.AUTOR, e.ROK_VYDANI, "
                    + "e.ROCNIK_PERIODIKA, e.MISTO_VYDANI"
                    + " from "
                    + "    (select "
                    + "    i.SIGLA,"
                    + "    r1.UPDATE_TIMESTAMP,r1.CISLO_RDCZ, r1.URNNBN,"
                    + "    r1.PRIDELENO_DNE, r1.FORMAT, r1.DOSTUPNOST, "
                    + "    r1.CISLO_ZAKAZKY, r1.FINANCOVANO, r1.INTELEKTUALNI_ENTITA"
                    + "    from "
                    + "    URNNBN.DIGITALNI_REPREZENTACE  r1 left outer join URNNBN.INSTITUCE  i"
                    + "    on r1.INSTITUCE=i.INSTITUCE_ID) r left outer join URNNBN.INTELEKTUALNI_ENTITA e"
                    + "                                      on r.INTELEKTUALNI_ENTITA=e.IE_ID "
                    + "                                      where e.DRUH_DOKUMENTU='GP'";
            ResultSet resultSet = st.executeQuery(statement);
            buildImports(resultSet);
        } catch (SQLException ex) {
            Logger.getLogger(RecordImportBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void buildImports(ResultSet resultSet) throws SQLException {
        int counter = 0;
        while (resultSet.next()) {
            try {
                buildImport(resultSet);
            } catch (Throwable ex) {
                Logger.getLogger(RecordImportBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (++counter > 3) {
                break;
            }





        }
    }

    final Element appendElementWithContentIfNotNull(Element root, Object content, String elementName) {
        if (content != null) {
            Element child = addElement(root, elementName);
            child.appendChild(String.valueOf(content));
            return child;
        } else {
            return null;
        }
    }

    Element addElement(Element root, String elementName) {
        Element child = new Element(NAMESPACE_PREFIX + ":" + elementName, RESOLVER);
        //Element child = new Element(elementName, RESOLVER);
        root.appendChild(child);
        return child;
    }

    private void buildImport(ResultSet resultSet) throws SQLException, Exception {
        String urnNbn = updateUrn(enhanceString(resultSet.getString("URNNBN")));
        String registrarCode = updateRegistrarCode(enhanceString(resultSet.getString("SIGLA")));
        checkIfFits(urnNbn, registrarCode);
        Element importEl = new Element("r:import", RESOLVER);
        appendEntityElement(importEl, resultSet);
        appendDigitalDocumentElement(importEl, resultSet, urnNbn);

        saveImportDocToFile(new Document(importEl), urnNbn);
        //k pozdejsi aktualizaci databaze mimo importy
        saveDateStamps(resultSet, urnNbn);

        //patri k ie a stejne tam nic moc neni, tak asi zahodit
        String accessibility = enhanceString(resultSet.getString("DOSTUPNOST"));
    }

    private String updateUrn(String original) {
        if (original.toLowerCase().startsWith("urn:nbn:cz:aba001")) {
            return "urn:nbn:cz:tst001-" + original.split("-")[1];
        } else {
            return original;
        }
    }

    private String updateRegistrarCode(String original) {
        if (original.toLowerCase().equals("aba001")) {
            return "tst001";
        } else {
            return original;
        }
    }
    
    

    private void checkIfFits(String urnNbn, String registrarCode) throws Exception {      
        if (!urnNbn.toLowerCase().substring(URNNBN_PREFIX.length()).startsWith(registrarCode.toLowerCase())) {
            throw new Exception(urnNbn + " dowsn't fit " + registrarCode);
        }
    }

    private String enhanceString(String original) {
        if (original == null || original.isEmpty()) {
            return null;
        }
        String normalizedSpaces = normalizeSpaces(original);        
        return normalizedSpaces.trim();
    }

    private String normalizeSpaces(String string) {
        while (string.contains(TWO_SPACES)) {
            string = string.replace(TWO_SPACES, SPACE);
        }
        return string;
    }


    private void appendDigitalDocumentElement(Element importEl, ResultSet resultSet, String urnNbn) throws SQLException {
        Element docEl = addElement(importEl, "digitalDocument");
        //urn
        appendElementWithContentIfNotNull(docEl, urnNbn, "urnNbn");
        Element identifiers = addElement(docEl, "registrarScopeIdentifiers");
        //registrar scope id 
        String rdNumber = enhanceString(resultSet.getString("CISLO_RDCZ"));
        if (rdNumber != null) {
            Element idEl = appendElementWithContentIfNotNull(identifiers, rdNumber, "id");
            idEl.addAttribute(new Attribute("type", "RDCZ"));
        }
        //financed
        String financed = enhanceString(resultSet.getString("FINANCOVANO"));
        appendElementWithContentIfNotNull(docEl, financed, "financed");
        //contract number
        String contractNumber = enhanceString(resultSet.getString("CISLO_ZAKAZKY"));
        appendElementWithContentIfNotNull(docEl, contractNumber, "contractNumber");
        //format
        String ddFormat = enhanceString(resultSet.getString("FORMAT"));
        if (ddFormat != null) {
            Element technical = addElement(docEl, "technicalMetadata");
            appendElementWithContentIfNotNull(technical, ddFormat, "format");
        }
    }

    private void appendEntityElement(Element importEl, ResultSet resultSet) throws SQLException {
        //nepouzit, v selektu
        //String documentType = resultSet.getString("DRUH_DOKUMENTU");

        //intelectual entity type
        String entityType = "monograph";
        Element entityEl = addElement(importEl, entityType);

        //title info
        Element titleInfo = addElement(entityEl, "titleInfo");

        //ruzne mapovani u ruznych IE (issue title napr. )
        String entityTitle = enhanceString(resultSet.getString("NAZEV"));
        appendElementWithContentIfNotNull(titleInfo, entityTitle, "title");

        //volumeTitle
        String periodicalVolume = resultSet.getString("ROCNIK_PERIODIKA");
        appendElementWithContentIfNotNull(titleInfo, periodicalVolume, "volumeTitle");

        //isbn
        String ccnb = enhanceString(resultSet.getString("CCNB"));
        appendElementWithContentIfNotNull(entityEl, ccnb, "ccnb");

        //issn
        String issn = enhanceString(resultSet.getString("ISSN"));
        appendElementWithContentIfNotNull(entityEl, issn, "issn");

        //menit za behu
        String documentType = "grafika";
        appendElementWithContentIfNotNull(entityEl, documentType, "documentType");

        //author
        String author = enhanceString(resultSet.getString("AUTOR"));
        Element primaryOriginatorEl = appendElementWithContentIfNotNull(entityEl, author, "primaryOriginator");
        if (primaryOriginatorEl != null) {
            primaryOriginatorEl.addAttribute(new Attribute("type", "AUTHOR"));
        }

        //publication
        String publishmentYear = enhanceString(resultSet.getString("ROK_VYDANI"));
        String publishmentPlace = enhanceString(resultSet.getString("MISTO_VYDANI"));
        if (publishmentPlace != null || publishmentYear != null) {
            Element publication = addElement(importEl, "publication");
            appendElementWithContentIfNotNull(publication, publishmentPlace, "place");
            appendElementWithContentIfNotNull(publication, publishmentYear, "year");
        }

    }

    private void saveDateStamps(ResultSet resultSet, String urnNbn) throws SQLException, IOException {
        Document doc = datestampsDocument(resultSet, urnNbn);
        String path = datastampDir.getAbsolutePath() + File.separator + urnNbn + ".xml";
        saveDocumentToFile(doc, path);
    }

    private Document datestampsDocument(ResultSet resultSet, String urnNbn) throws SQLException {
        Element datastamps = new Element("dateStamps");
        datastamps.addAttribute(new Attribute("id", urnNbn));
        String urnAssigned = enhanceString(resultSet.getString("URN_prideleno"));
        appendElementWithContentIfNotNull(datastamps, urnAssigned, "urn");
        String ieUpdated = enhanceString(resultSet.getString("IE_updated"));
        appendElementWithContentIfNotNull(datastamps, ieUpdated, "intelectualEntity");
        String ddUpdated = enhanceString(resultSet.getString("DD_UPDATED"));
        appendElementWithContentIfNotNull(datastamps, ddUpdated, "digitalDocument");
        return new Document(datastamps);
    }

    private void saveImportDocToFile(Document document, String urnNbn) throws IOException {
        String path = resultDir.getAbsolutePath() + File.separator + urnNbn + ".xml";
        saveDocumentToFile(document, path);
    }
    
    private void saveDocumentToFile(Document document, String path) throws IOException {
        FileOutputStream out = new FileOutputStream(path);
        Serializer ser = new Serializer(out, "UTF-8");
        ser.setIndent(2);
        ser.write(document);         
    }
}

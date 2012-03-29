/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.legacyrecordsimport;

import cz.nkp.urnnbn.legacyrecordsimport.validation.ImportValidator;
import cz.nkp.urnnbn.xml.commons.Namespaces;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import org.xml.sax.SAXException;

/**
 *
 * @author Martin Řehánek
 */
public class RecordImportBuilder {

    public static final String DOCUMENT_TYPE_BOOK = "kniha";
    public static final String DOCUMENT_TYPE_MAP = "mapa";
    public static final String DOCUMENT_TYPE_MUSIC = "hudebnina";
    public static final String DOCUMENT_TYPE_GRAPHICS = "grafika";
    public static final String DOCUMENT_TYPE_PERIODICAL = "noviny";
    public static final String DOCUMENT_TYPE_PERIODICAL_VOLUME = "rocnik novin";
    

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
            String statement = "select  r.DR_ID, r.UPDATE_TIMESTAMP as DD_updated,r.CISLO_RDCZ, r.URNNBN,"
                    + "r.PRIDELENO_DNE as URN_prideleno, r.FORMAT, r.DOSTUPNOST, r.SIGLA,"
                    + "r.CISLO_ZAKAZKY, r.FINANCOVANO, e.UPDATE_TIMESTAMP as IE_updated,"
                    + "e.CCNB, e.ISSN, e.DRUH_DOKUMENTU, e.NAZEV, e.AUTOR, e.ROK_VYDANI, "
                    + "e.ROCNIK_PERIODIKA, e.MISTO_VYDANI, z.URL"
                    + " from "
                    + "    (select "
                    + "    i.SIGLA, r1.DR_ID,"
                    + "    r1.UPDATE_TIMESTAMP,r1.CISLO_RDCZ, r1.URNNBN,"
                    + "    r1.PRIDELENO_DNE, r1.FORMAT, r1.DOSTUPNOST, "
                    + "    r1.CISLO_ZAKAZKY, r1.FINANCOVANO, r1.INTELEKTUALNI_ENTITA"
                    + "    from "
                    + "    URNNBN.DIGITALNI_REPREZENTACE  r1 left outer join URNNBN.INSTITUCE  i"
                    + "    on r1.INSTITUCE=i.INSTITUCE_ID) r left outer join URNNBN.INTELEKTUALNI_ENTITA e"
                    + "                                      on r.INTELEKTUALNI_ENTITA=e.IE_ID "
                    + " left join URNNBN.ZVEREJNENO z on r.DR_ID =z.DIGITALNI_REPREZENTACE";
                    //+ "                                      where e.DRUH_DOKUMENTU='BK'";
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
            } catch (IOException ex) {
                System.out.println("EXC:IO " + ex.getMessage());
            }
            System.out.println(counter++);


//            if (++counter > 800) {
//                break;
//            }

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

    private void buildImport(ResultSet resultSet) throws SQLException, IOException {
        //String urnNbn = updateUrn(enhanceString(resultSet.getString("URNNBN")));
        //String registrarCode = updateRegistrarCode(enhanceString(resultSet.getString("SIGLA")));
        String urnNbn = enhanceString(resultSet.getString("URNNBN"));
        //if(urnNbn)
        String registrarCode = enhanceString(resultSet.getString("SIGLA"));
        try {
            checkIfFits(urnNbn, registrarCode);
        } catch (SiglaNotFoundException ex) {
            System.out.println(ex.getMessage());
            return;
            
        }
        Element importEl = new Element("r:import", RESOLVER);
        String documentTypeString = resultSet.getString("DRUH_DOKUMENTU");
        String issn = resultSet.getString("ISSN");
        String periodicalVolume = resultSet.getString("ROCNIK_PERIODIKA");
        if(documentTypeString == null) {
            return;
        }
        String documentType = "";
        if(documentTypeString.equals("BK") && periodicalVolume == null && issn == null) {
            documentType = DOCUMENT_TYPE_BOOK;
        } else if((documentTypeString.equals("BK") || documentTypeString.equals("SE")) && periodicalVolume != null) {
            documentType = DOCUMENT_TYPE_PERIODICAL_VOLUME;
        } else if((documentTypeString.equals("BK") || documentTypeString.equals("SE")) && periodicalVolume == null) {
            documentType = DOCUMENT_TYPE_PERIODICAL;
        } else if(documentTypeString.equals("GP")) {
            documentType = DOCUMENT_TYPE_PERIODICAL;
        }else if(documentTypeString.equals("MU")) {
            documentType = DOCUMENT_TYPE_MUSIC;
        }else if(documentTypeString.equals("MP")) {
            documentType = DOCUMENT_TYPE_MAP;
        }
        
        
        appendEntityElement(importEl, resultSet, documentType);
        appendDigitalDocumentElement(importEl, resultSet, urnNbn);

        Document importDocument = new Document(importEl);
        validateDocument(importDocument, urnNbn);
        
        
        String path = resultDir.getAbsolutePath() + File.separator + getEntityType(documentType) + File.separator + registrarCode + File.separator + urnNbn + ".xml";
        saveDocumentToFile(importDocument, path);
    
        //k pozdejsi aktualizaci databaze mimo importy
        //saveDateStamps(resultSet, urnNbn);

        //patri k ie a stejne tam nic moc neni, tak asi zahodit        
        String accessibility = enhanceString(resultSet.getString("DOSTUPNOST"));
        if(accessibility != null) {
            accessibility = "volně přístupné";
        }
        String url = enhanceString(resultSet.getString("URL"));
        String format = enhanceString(resultSet.getString("FORMAT"));
        try {
            buildDigitalInstanceDocument(url, registrarCode, format, accessibility, urnNbn, documentType);
        } catch (SiglaNotFoundException ex) {
            System.out.println("");
        }
            
        
    }
    
    private void buildDigitalInstanceDocument(String url, String registrarCode, String format, 
            String accessibility, String urnNbn, String documentType) throws SiglaNotFoundException, IOException {
        
        if(url == null) { 
            return;
        }
        if(url.startsWith("\"")) {
            System.out.println("oprava url " + url);
            url = url.substring(1, url.length());
            System.out.println("oprava url " + url);
        }
        if(!url.startsWith("http://")) {
            System.out.println("spatne url " + url);
            return;
        }
        int digitalLibraryId = SiglaLibraryIdMapping.getLibraryId(registrarCode);

        Element rootEl = new Element("r:digitalInstance", RESOLVER);
        
        appendElementWithContentIfNotNull(rootEl, url, "url");
        appendElementWithContentIfNotNull(rootEl, digitalLibraryId, "digitalLibraryId");
        appendElementWithContentIfNotNull(rootEl, format, "format");
        appendElementWithContentIfNotNull(rootEl, accessibility, "accessibility");       
        

        Document instanceDocument = new Document(rootEl);
        
        //String path = resultDir.getAbsolutePath()+ File.separator + "instances"+ File.separator + getEntityType(documentType) + File.separator + registrarCode + File.separator + urnNbn + "-add.xml";
        String path = resultDir.getAbsolutePath()+ getEntityType(documentType) + File.separator + registrarCode + File.separator + urnNbn + "-add.xml";
        saveDocumentToFile(instanceDocument, path);
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
    
    

    private void checkIfFits(String urnNbn, String registrarCode) throws SiglaNotFoundException {      
        if(urnNbn.length() < 12) { 
            throw new SiglaNotFoundException("EXC:urnnmb not valid: " + urnNbn);
        }
        if (!urnNbn.toLowerCase().substring(URNNBN_PREFIX.length()).startsWith(registrarCode.toLowerCase())) {
            throw new SiglaNotFoundException("EXC:" + urnNbn + " dowsn't fit " + registrarCode);
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
       //TODO - spatne schema nebo toto
       //if (ddFormat != null) {
            Element technical = addElement(docEl, "technicalMetadata");
            appendElementWithContentIfNotNull(technical, ddFormat, "format");
       // }
    }
    
    
    private String getEntityType(String docomuntType) {
        if(DOCUMENT_TYPE_BOOK.equals(docomuntType)) {
            return "monograph";
        }
        if(DOCUMENT_TYPE_PERIODICAL.equals(docomuntType)) {
            return "periodical";
        }
        if(DOCUMENT_TYPE_PERIODICAL_VOLUME.equals(docomuntType)) {
            return "periodicalVolume";
        }
        return "otherEntity";        
    }

    private void appendEntityElement(Element importEl, ResultSet resultSet, String documentType) throws SQLException {
                
        String entityType = getEntityType(documentType);
        Element entityEl = addElement(importEl, entityType);

        //title info
        Element titleInfo = addElement(entityEl, "titleInfo");

        //ruzne mapovani u ruznych IE (issue title napr. )
        String entityTitle = enhanceString(resultSet.getString("NAZEV"));
        
        if(documentType.equals(DOCUMENT_TYPE_PERIODICAL_VOLUME)) {
            String periodicalVolume = resultSet.getString("ROCNIK_PERIODIKA");
            appendElementWithContentIfNotNull(titleInfo, entityTitle, "periodicalTitle");
            appendElementWithContentIfNotNull(titleInfo, "ročník " + periodicalVolume, "volumeTitle");
        } else {
            appendElementWithContentIfNotNull(titleInfo, entityTitle, "title");
        }

        //volumeTitle
        
        //appendElementWithContentIfNotNull(titleInfo, periodicalVolume, "volumeTitle");

        //isbn
        String ccnb = enhanceString(resultSet.getString("CCNB"));
        appendElementWithContentIfNotNull(entityEl, ccnb, "ccnb");

        //issn
        String issn = enhanceString(resultSet.getString("ISSN"));
        if(issn != null && issn.matches("\\d{4}-\\d{3}[0-9Xx]{1}")) {
            appendElementWithContentIfNotNull(entityEl, issn, "issn");
        }

        //menit za behu
        
        appendElementWithContentIfNotNull(entityEl, documentType, "documentType");

        //author
        String author = enhanceString(resultSet.getString("AUTOR"));
        Element primaryOriginatorEl = appendElementWithContentIfNotNull(entityEl, author, "primaryOriginator");
        if (primaryOriginatorEl != null) {
            primaryOriginatorEl.addAttribute(new Attribute("type", "AUTHOR"));
        }

        //publication
        String publishmentYear = enhanceString(resultSet.getString("ROK_VYDANI"));
        if (publishmentYear != null) {
            if(publishmentYear.startsWith("[")) {
                publishmentYear = publishmentYear.substring(1, publishmentYear.length());
            }
            if(publishmentYear.endsWith("]")) {
                publishmentYear = publishmentYear.substring(0, publishmentYear.length() - 1);
            }

            if (!publishmentYear.matches("[0-9Xx]{4}") || documentType.equals(DOCUMENT_TYPE_PERIODICAL)) {
                publishmentYear = null;
            }

        }
        String publishmentPlace = enhanceString(resultSet.getString("MISTO_VYDANI"));
        if (publishmentPlace != null || publishmentYear != null) {
            Element publication = addElement(entityEl, "publication");
            appendElementWithContentIfNotNull(publication, publishmentPlace, "place");
            appendElementWithContentIfNotNull(publication, publishmentYear, "year");
        }
        

    }

    private void saveDateStamps(ResultSet resultSet, String urnNbn) throws SQLException, IOException {
        Document document = datestampsDocument(resultSet, urnNbn);
        String path = datastampDir.getAbsolutePath() + File.separator + urnNbn + ".xml";
        saveDocumentToFile(document, path);
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


    
    private void saveDocumentToFile(Document document, String path) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(path);
        Serializer ser = new Serializer(out, "UTF-8");
        ser.setIndent(2);
        ser.write(document);         
    }
    
    private void validateDocument(Document document, String urnNbn) {
        System.out.println("validating " + urnNbn);
        try {
            ImportValidator.validate(document);
        } catch (SAXException ex) {
            Logger.getLogger(RecordImportBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(RecordImportBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParsingException ex) {
            Logger.getLogger(RecordImportBuilder.class.getName()).log(Level.SEVERE, null, ex);        
        } catch (IOException ex) {
            Logger.getLogger(RecordImportBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

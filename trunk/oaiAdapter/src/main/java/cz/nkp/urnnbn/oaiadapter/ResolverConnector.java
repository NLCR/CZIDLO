/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.oaiadapter.utils.XmlTools;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.XPathContext;

/**
 *
 * @author hanis
 */
public class ResolverConnector {

    public static enum UrnnbnStatus {

        FREE, RESERVED, ACTIVE, UNDEFINED
    }
    public static final String IMPORT_TEMPLATE_URL = "http://urnnbn-resolver-v2.googlecode.com/svn/trunk/xml/src/main/resources/xsd/recordImport.xsd.xml";
    public static final String DIGITAL_INSTANCE_TEMPLATE_URL = "http://urnnbn-resolver-v2.googlecode.com/svn/trunk/xml/src/main/resources/xsd/digitalInstanceImport.xsd.xml";
    public static final String ERROR_CODE_REGISTAR = "UNKNOWN_REGISTRAR";
    public static final String ERROR_CODE_DOCUMENT = "UNKNOWN_DIGITAL_DOCUMENT";
    public static final String RESOLVER_NAMESPACE = "http://resolver.nkp.cz/v2/";
    public static final String RESOLVER_BASE_URL = "resolver-test.nkp.cz/";
    public static final String RESOLVER_API_URL = RESOLVER_BASE_URL + "api/v2/";

    private static String getDigitalDocumentUrl(String registrar, String identifier, String registarScopeId) {
        String url = "http://" + RESOLVER_API_URL + "registrars/" + registrar
                + "/digitalDocuments/id/" + registarScopeId + "/"
                + identifier + "?format=xml";

        return url;
    }

    private static String getImportDocumetUrl(String registrarCode) {
        String url = "https://" + RESOLVER_API_URL + "registrars/" + registrarCode
                + "/digitalDocuments";
        return url;
    }

    private static String getUrnnbnRegistrationUrl(String registrarCode, int size) {
        String url = "https://" + RESOLVER_API_URL + "registrars/" + registrarCode
                + "/urnNbnReservations?size=" + size;
        return url;
    }

    private static String getUrnnbnStatusUrl(String urnnbn) {
        String url = "http://" + RESOLVER_API_URL + "urnnbn/" + urnnbn;
        return url;
    }

    private static String getDigitalInsatancesUrl(String urnnbn) {
        String url = "http://" + RESOLVER_API_URL + "resolver/" + urnnbn + "/digitalInstances";
        return url;
    }

    private static String getImportDigitalInstanceUrl(String urnnbn) {
        String url = "https://" + RESOLVER_API_URL + "resolver/" + urnnbn
                + "/digitalInstances";
        return url;
    }

    private static String getUpdateRegistrarScopeIdUrl(String urnnbn, String registrarScopeId) {
        String url = "https://" + RESOLVER_API_URL + "resolver/" + urnnbn
                + "/identifiers/" + registrarScopeId;
        return url;
    }

    private static String getRemoveDigitalInstanceUrl(String id) {
        String url = "https://" + RESOLVER_API_URL + "digitalInstances/id/" + id;
        return url;
    }

    private static String getDigitalInstanceUrl(String id) {
        String url = "http://" + RESOLVER_API_URL + "digitalInstances/id/" + id;
        return url;
    }

    public static boolean isDocumentAlreadyImported(String registrar, String identifier, String registarScopeId) throws IOException, ParsingException {
        String url = getDigitalDocumentUrl(registrar, identifier, registarScopeId);
        Document document = XmlTools.getDocument(url, true);
        Element rootElement = document.getRootElement();
        if ("digitalDocument".equals(rootElement.getLocalName())) {
            return true;
        } else if ("error".equals(rootElement.getLocalName())) {
            XPathContext context = new XPathContext("oai", RESOLVER_NAMESPACE);
            Nodes codeNode = rootElement.query("//oai:code", context);
            if (codeNode.size() > 0) {
                String code = codeNode.get(0).getValue();
                //System.out.println("code:" + code);
                if (!(ERROR_CODE_DOCUMENT.equals(code) || ERROR_CODE_REGISTAR.equals(code))) {
                    //TODO spatne error code - neco je spatne ...staci kontrolovat jen tyto dva kody?                    
                    throw new RuntimeException();
                } else {
                    return false;
                }
            } else {
                //TODO spatna struktura dokumentu
                throw new RuntimeException();
            }
        } else {
            //TODO spatna struktura dokumentu
            throw new RuntimeException();
        }
    }

    public static List<String> getDigitailInstancesIdList(String urnnbn) throws IOException, ParsingException {
        List<String> list = new ArrayList<String>();
        String url = getDigitalInsatancesUrl(urnnbn);
        Document document = XmlTools.getDocument(url, false);
        Element rootElement = document.getRootElement();
        XPathContext context = new XPathContext("oai", RESOLVER_NAMESPACE);
        Nodes idNodes = rootElement.query("//oai:digitalInstance/oai:id", context);
        for (int i = 0; i < idNodes.size(); i++) {
            list.add(idNodes.get(i).getValue());
        }
        return list;
    }

    public static Document getDigitailInstanceById(String id) throws IOException, ParsingException {
        List<String> list = new ArrayList<String>();
        String url = getDigitalInstanceUrl(id);
        Document document = XmlTools.getDocument(url, false);
        return document;
    }

    public static UrnnbnStatus getUrnnbnStatus(String urnnbn) throws IOException, ParsingException {
        String url = getUrnnbnStatusUrl(urnnbn);
        Document document = XmlTools.getDocument(url, true);
        Element rootElement = document.getRootElement();
        XPathContext context = new XPathContext("oai", RESOLVER_NAMESPACE);
        Nodes statusNode = rootElement.query("//oai:status", context);
        if (statusNode.size() > 0) {
            String status = statusNode.get(0).getValue();
            if ("FREE".equals(status)) {
                return ResolverConnector.UrnnbnStatus.FREE;
            } else if ("RESERVED".equals(status)) {
                return ResolverConnector.UrnnbnStatus.RESERVED;
            } else if ("ACTIVE".equals(status)) {
                return ResolverConnector.UrnnbnStatus.ACTIVE;
            }
        }
        return ResolverConnector.UrnnbnStatus.UNDEFINED;
    }

    public static List<String> reserveUrnnbnBundle(String registarCode, int bundleSize, String login, String password) throws IOException, ResolverConnectionException, ParsingException {
        List<String> urnnbnList = new ArrayList<String>();
        String url = getUrnnbnRegistrationUrl(registarCode, bundleSize);
        HttpsURLConnection connection = XmlTools.getAuthConnection(login, password, url, "POST", true);
        int responseCode = connection.getResponseCode();
        if (responseCode != 201) { //TODO pokud ok, pak vzdy 200??
            throw new ResolverConnectionException("URNNBN reservation: response code expected 201,  found " + responseCode);
        }
        InputStream is = connection.getInputStream();
        Builder builder = new Builder();
        Document responseDocument = builder.build(is);
        Element rootElement = responseDocument.getRootElement();
        XPathContext context = new XPathContext("oai", RESOLVER_NAMESPACE);

        Nodes nodes = rootElement.query("//oai:urnNbn", context);
        System.out.println(responseDocument.toXML());
        System.out.println(rootElement.toXML());
        System.out.println(nodes.size());
        for (int i = 0; i < nodes.size(); i++) {
            urnnbnList.add(nodes.get(i).getValue());
        }
        return urnnbnList;

    }

    public static String importDocument(Document document, String registarCode, String login, String password) throws IOException, ParsingException, ResolverConnectionException {
        String url = getImportDocumetUrl(registarCode);
        HttpsURLConnection connection = XmlTools.getAuthConnection(login, password, url, "POST", true);
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(document.toXML());
        wr.flush();
        wr.close();
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) { //TODO pokud ok, pak vzdy 200??            
            throw new ResolverConnectionException("Importing record document: response code != 200");
        }
        //System.out.println("import code" + connection.getResponseCode());

        InputStream is = connection.getInputStream();
        Builder builder = new Builder();
        Document responseDocument = builder.build(is);
        String urnnbn = ResolverConnector.getAllocatedURNNBN(responseDocument);
        return urnnbn;
    }

    public static void importDigitalInstance(Document document, String urnnbn, String login, String password)
            throws IOException, ParsingException, ResolverConnectionException {
        String url = getImportDigitalInstanceUrl(urnnbn);
        HttpsURLConnection connection = XmlTools.getAuthConnection(login, password, url, "POST", true);
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(document.toXML());
        wr.flush();
        wr.close();
        int responseCode = connection.getResponseCode();
        if (responseCode != 201) { //TODO pokud ok, pak vzdy 201??
            throw new ResolverConnectionException("Putting digital instance: response code != 201");
        }

    }

    public static void putRegistrarScopeIdentifier(String urnnbn, String documentId, String registrarScopeId, String login, String password)
            throws IOException, ResolverConnectionException {
        String url = getUpdateRegistrarScopeIdUrl(urnnbn, registrarScopeId);
        HttpsURLConnection connection = XmlTools.getAuthConnection(login, password, url, "PUT", true);
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(documentId);
        wr.flush();
        wr.close();
        int responseCode = connection.getResponseCode();
        if (responseCode != 201) { //TODO pokud ok, pak vzdy 201??
            throw new ResolverConnectionException("Puttin registrar scope identifier: response code != 201");
        }

    }

    public static void removeDigitalInstance(String id, String login, String password)
            throws IOException, ResolverConnectionException {
        String url = getRemoveDigitalInstanceUrl(id);
        HttpsURLConnection connection = XmlTools.getAuthConnection(login, password, url, "DELETE", false);
        int responseCode = connection.getResponseCode();
        System.out.println("rc: " + responseCode);
        if (responseCode != 200) {
            throw new ResolverConnectionException("Removing digital instance: response code != 200");
        }
    }

    private static String writeInputStream(InputStream is) {
        Builder builder = new Builder();
        try {
            Document responseDocument = builder.build(is);
            return "RD:" + responseDocument.toXML();
        } catch (ValidityException ex) {
            return "V:" + ex.getMessage();
        } catch (ParsingException ex) {
            return "P:" + ex.getMessage();
        } catch (IOException ex) {
            return "IO:" + ex.getMessage();
        }
    }

    public static String getAllocatedURNNBN(Document document) {
        Element rootElement = document.getRootElement();
        XPathContext context = new XPathContext("oai", RESOLVER_NAMESPACE);
        Nodes node = rootElement.query("//oai:value", context);
        if (node.size() < 1) {
            //TODO spatna struktura dokumentu
            throw new RuntimeException();
        }
        return node.get(0).getValue();
    }

    private static List<String> getDigitalInstancesIdListByLibrary(String urnnbn, String libraryId) throws IOException, ParsingException {
        List<String> newIdList = new ArrayList<String>();
        List<String> idList = getDigitailInstancesIdList(urnnbn);
        for (String id : idList) {
            Document document = getDigitailInstanceById(id);
            XPathContext context = new XPathContext("oai", RESOLVER_NAMESPACE);
            Nodes nodes = document.query("//oai:digitalLibrary/oai:id", context);
            if (nodes.size() > 0) {
                String docLibraryId = nodes.get(0).getValue();
                if(docLibraryId.endsWith(libraryId)) {
                    newIdList.add(id);
                }
            }
        }
        return newIdList;
    }

    public static void main(String[] args) {
        try {
            //List<String> ids = ResolverConnector.getDigitailInstancesIdList("urn:nbn:cz:duha-0000vn");
            List<String> ids = ResolverConnector.getDigitalInstancesIdListByLibrary("urn:nbn:cz:duha-0000vn", "52");
            for (String string : ids) {
                System.out.println(string);
            }
        } catch (IOException ex) {
            Logger.getLogger(ResolverConnector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParsingException ex) {
            Logger.getLogger(ResolverConnector.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}

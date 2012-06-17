/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.oaiadapter.utils.XmlTools;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import javax.net.ssl.HttpsURLConnection;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.XPathContext;

/**
 *
 * @author hanis
 */
public class ResolverConnector {

    public static final String IMPORT_TEMPLATE_URL = "http://urnnbn-resolver-v2.googlecode.com/svn/trunk/xml/src/main/resources/xsd/recordImport.xsd.xml";
    public static final String DIGITAL_INSTANCE_TEMPLATE_URL = "http://urnnbn-resolver-v2.googlecode.com/svn/trunk/xml/src/main/resources/xsd/digitalInstanceImport.xsd.xml";
    public static final String ERROR_CODE_REGISTAR = "UNKNOWN_REGISTRAR";
    public static final String ERROR_CODE_DOCUMENT = "UNKNOWN_DIGITAL_DOCUMENT";
    public static final String RESOLVER_NAMESPACE = "http://resolver.nkp.cz/v2/";
    public static final String RESOLVER_BASE_URL = "resolver-test.nkp.cz/";//api/v2/registrars/tst02/digitalDocuments/id/K4_pid/uuid:123?format=xml
    public static final String RESOLVER_API_URL = RESOLVER_BASE_URL + "api/v2/";
    //public static final String OAI_ADAPTER_ID = "OAI_Adapter";
    //public static final String OAI_ADAPTER_ID = "K4_pid";

    //private static final String RESOLVER_DIGITAL_DOCUMENT_URL = RESOLVER_API_URL + 
    //private static final String http://resolver.nkp.cz/api/v2/registrars/tst02/digitalDocuments/id/K4_pid/uuid:123?format=xml
    public static String getDigitalDocumentUrl(String registrar, String identifier, String registarScopeId) {
        String url = "http://" + RESOLVER_API_URL + "registrars/" + registrar
                + "/digitalDocuments/id/" + registarScopeId + "/"
                + identifier + "?format=xml";

        return url;
    }
    //http://resolver-test.nkp.cz/registrars/tsh01/digitalDocuments/id/OAI_Adapter/?format=xml

    public static String getImportDocumetUrl(String registrarCode) {
        String url = "https://" + RESOLVER_API_URL + "registrars/" + registrarCode
                + "/digitalDocuments";
        return url;
    }

    public static boolean isDocumentAlreadyImported(String registrar, String identifier, String registarScopeId) throws IOException, ParsingException {
        String url = getDigitalDocumentUrl(registrar, identifier, registarScopeId);
        //System.out.println(url);
        //System.out.println(url);
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
        // System.out.println(responseDocument.toXML());
    }

    public static void importDigitalInstance(Document document, String urnnbn, String login, String password) 
            throws IOException, ParsingException, ResolverConnectionException {
        String url = getImportDigitalInstanceUrl(urnnbn);
        //System.out.println(url);
        //System.out.println(document.toXML());
        HttpsURLConnection connection = XmlTools.getAuthConnection(login, password, url, "POST", true);
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(document.toXML());
        wr.flush();
        wr.close();
        //System.out.println("dig instance code" + connection.getResponseCode());
        int responseCode = connection.getResponseCode();
        if (responseCode != 201) { //TODO pokud ok, pak vzdy 201??
            throw new ResolverConnectionException("Puttin digital instance: response code != 201");
        }

    }

    public static String getImportDigitalInstanceUrl(String urnnbn) {
        String url = "https://" + RESOLVER_API_URL + "resolver/" + urnnbn
                + "/digitalInstances";
        return url;
    }

    public static String getUpdateRegistrarScopeIdUrl(String urnnbn, String registrarScopeId) {
        String url = "https://" + RESOLVER_API_URL + "resolver/" + urnnbn
                + "/identifiers/" + registrarScopeId;
        return url;
    }

    public static void putRegistrarScopeIdentifier(String urnnbn, String documentId, String registrarScopeId, String login, String password) 
            throws  IOException, ResolverConnectionException {
        String url = getUpdateRegistrarScopeIdUrl(urnnbn, registrarScopeId);
        //System.out.println(url + " - " + documentId);

        HttpsURLConnection connection = XmlTools.getAuthConnection(login, password, url, "PUT", true);
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(documentId);
        wr.flush();
        wr.close();
        //System.out.println("Put: " + connection.getResponseCode());
        //System.out.println("Put: " + connection.getResponseCode());        
        int responseCode = connection.getResponseCode();
        if (responseCode != 201) { //TODO pokud ok, pak vzdy 201??
            throw new ResolverConnectionException("Puttin registrar scope identifier: response code != 201");
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

    public static void main(String[] args) {
//        String registrar = "tst02";
//        String identifier = "uuid:123";
//        try {
//            boolean result = ResolverConnector.isDocumentAlreadyImported(registrar, identifier);
//            System.out.println("result: " + result);
//        } catch (IOException ex) {
//            Logger.getLogger(ResolverConnector.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ParsingException ex) {
//            Logger.getLogger(ResolverConnector.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}

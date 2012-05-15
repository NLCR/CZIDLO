/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.oaiadapter.utils.XmlTools;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public static final String ERROR_CODE_REGISTAR = "UNKNOWN_REGISTRAR";
    public static final String ERROR_CODE_DOCUMENT = "UNKNOWN_DIGITAL_DOCUMENT";
    public static final String RESOLVER_NAMESPACE = "http://resolver.nkp.cz/v2/";
    public static final String RESOLVER_BASE_URL = "http://resolver.nkp.cz/";//api/v2/registrars/tst02/digitalDocuments/id/K4_pid/uuid:123?format=xml
    public static final String RESOLVER_API_URL = RESOLVER_BASE_URL + "api/v2/";
    //public static final String OAI_ADAPTER_ID = "OAI_Adapter";
    public static final String OAI_ADAPTER_ID = "K4_pid";

    //private static final String RESOLVER_DIGITAL_DOCUMENT_URL = RESOLVER_API_URL + 
    //private static final String http://resolver.nkp.cz/api/v2/registrars/tst02/digitalDocuments/id/K4_pid/uuid:123?format=xml
    public static String getDigitalDocumentUrl(String registrar, String identifier) {
        String url = RESOLVER_API_URL + "registrars/" + registrar
                + "/digitalDocuments/id/" + OAI_ADAPTER_ID + "/"
                + identifier + "?format=xml";

        return url;
    }

    public static boolean isDocumentAlreadyImported(String registrar, String identifier) throws IOException, ParsingException {
        Document document = XmlTools.getDocument(getDigitalDocumentUrl(registrar, identifier), true);
        Element rootElement = document.getRootElement();
        if ("digitalDocument".equals(rootElement.getLocalName())) {
            return true;
        } else if ("error".equals(rootElement.getLocalName())) {
            XPathContext context = new XPathContext("oai", RESOLVER_NAMESPACE);
            Nodes codeNode = rootElement.query("//oai:code", context);
            if (codeNode.size() > 0) {
                String code = codeNode.get(0).getValue();
                System.out.println("code:" + code);
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

    public static void main(String[] args) {
        String registrar = "tst02";
        String identifier = "uuid:123";
        try {
            boolean result = ResolverConnector.isDocumentAlreadyImported(registrar, identifier);
            System.out.println("result: " + result);
        } catch (IOException ex) {
            Logger.getLogger(ResolverConnector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParsingException ex) {
            Logger.getLogger(ResolverConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

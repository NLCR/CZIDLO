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
import nu.xom.ParsingException;

/**
 *
 * @author hanis
 */
public class ResolverConnector {    
    
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
        Document document = XmlTools.getDocument(getDigitalDocumentUrl(registrar, identifier));
        System.out.println(document.getRootElement().getLocalName());        
        return false;
    }
            
            
    
    public static void main(String[] args) {
        String registrar = "tst02";
        String identifier = "uuid:123d";
        try {
            ResolverConnector.isDocumentAlreadyImported(registrar, identifier);
        } catch (IOException ex) {
            Logger.getLogger(ResolverConnector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParsingException ex) {
            Logger.getLogger(ResolverConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
            
}

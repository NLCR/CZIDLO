/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter.utils;

import cz.nkp.urnnbn.oaiadapter.OaiAdapter;
import cz.nkp.urnnbn.oaiadapter.ResolverConnector;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;

/**
 *
 * @author hanis
 */
public class ImportDocumentHandler {
    
    private static final Logger logger = Logger.getLogger(ImportDocumentHandler.class.getName());
    
    
    public static Document putRegistrarScopeIdentifier(Document document, String oaiIdentifier) {
        Element root = document.getRootElement();
        Element digitalDocumentElement = root.getFirstChildElement("digitalDocument", ResolverConnector.RESOLVER_NAMESPACE);        
        if(digitalDocumentElement == null) {
            digitalDocumentElement = new Element("r:digitalDocument", ResolverConnector.RESOLVER_NAMESPACE);
            root.appendChild(digitalDocumentElement);
        }
        Element registrarScopeIdentifiersElement = digitalDocumentElement.getFirstChildElement("registrarScopeIdentifiers", ResolverConnector.RESOLVER_NAMESPACE);
        if(registrarScopeIdentifiersElement == null) {
            registrarScopeIdentifiersElement = new Element("r:registrarScopeIdentifiers", ResolverConnector.RESOLVER_NAMESPACE);
            int archiverIdPosition = digitalDocumentElement.indexOf(digitalDocumentElement.getFirstChildElement("archiverId", ResolverConnector.RESOLVER_NAMESPACE));
            int urnnbnPosition = digitalDocumentElement.indexOf(digitalDocumentElement.getFirstChildElement("urnNbn", ResolverConnector.RESOLVER_NAMESPACE));            
            int position = 0;
            if(urnnbnPosition != -1) {
                position = urnnbnPosition + 1;
            } else if(archiverIdPosition != -1) {
                position = archiverIdPosition + 1;
            }            
            digitalDocumentElement.insertChild(registrarScopeIdentifiersElement, position);
        }
        Element oaiAdapterScopeElement = new Element("r:id", ResolverConnector.RESOLVER_NAMESPACE);
        oaiAdapterScopeElement.addAttribute(new Attribute("type", OaiAdapter.REGISTAR_SCOPE_ID));
        oaiAdapterScopeElement.appendChild(oaiIdentifier);
        registrarScopeIdentifiersElement.appendChild(oaiAdapterScopeElement);
        return document;       
    }
    
    
    public static String getUrnnbnFromDocument(Document document) {
        Nodes nodes = document.query("/r:import/r:digitalDocument/r:urnNbn", ResolverConnector.CONTEXT);
        if(nodes.size() == 1) {
            return nodes.get(0).getValue();
        }
        return null;                
    }
    

    public static String getDigitalLibraryIdFromDocument(Document document) {
        Nodes nodes = document.query("/r:digitalInstance/r:digitalLibraryId", ResolverConnector.CONTEXT);
        if(nodes.size() == 1) {
            return nodes.get(0).getValue();
        }
        return null;                
    }
    
    
    
    public static void main(String[] args) {            
        //File file = new File("/home/hanis/prace/resolver/oai/parser-test/t.xml");
        File file = new File("/home/hanis/prace/resolver/oai/parser-test/docs/digitalDocument.xml");
        //File file = new File("/home/hanis/prace/resolver/oai/parser-test/monograph.xml");
        
        Builder builder = new Builder();
        Document doc = null;
        try {
            doc = builder.build(file);
        } catch (ParsingException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }                                
            Refiner.refineDocument(doc);
        ImportDocumentHandler.putRegistrarScopeIdentifier(doc, "oai:blablabla");
        //String urnnbn = ImportDocumentHandler.getUrnnbnFromDocument(doc);
        //System.out.println(urnnbn);
        System.out.println(doc.toXML());        
    }            
    
    
    
}

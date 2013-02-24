/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter.utils;

import cz.nkp.urnnbn.oaiadapter.DocumentOperationException;
import cz.nkp.urnnbn.oaiadapter.ResolverConnector;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

/**
 *
 * @author hanis
 */
public class Refiner {    

    private Refiner() {    
    }
    
    public static Document refineDocument(Document document) {
        Element root = document.getRootElement();
        if("import".equals(root.getLocalName())) {
            Refiner.parseImportElement(root);
        }                                    
        try {
            XmlTools.validateImport(document);
        } catch (DocumentOperationException ex) {
            Logger.getLogger(Refiner.class.getName()).log(Level.SEVERE, null, ex);
        }
        return document;       
    }
    
    
    
    
    private static void parseImportElement(Element importElement) {
        Elements importChildren = importElement.getChildElements();
        if(importChildren.size() > 0) {
            Element entityElement = importChildren.get(0);
            String entityName = entityElement.getLocalName();
            if("monograph".equals(entityName) ||
                    "monographVolume".equals(entityName) ||
                    "periodical".equals(entityName) ||
                    "periodicalVolume".equals(entityName) ||
                    "periodicalIssue".equals(entityName) ||
                    "analytical".equals(entityName) ||
                    "thesis".equals(entityName) ||
                    "otherEntity".equals(entityName)                                       
                    ) {
                Refiner.parseEntityElement(entityElement);            
            }
        }
        Element digitalDocumentElement = importElement.getFirstChildElement("digitalDocument", ResolverConnector.RESOLVER_NAMESPACE);
        if(digitalDocumentElement != null) {
            Refiner.parseDigitalDocumentElement(digitalDocumentElement);
        }            
    }
    
    private static void parseEntityElement(Element entityElement) {        
        Element titleInfoElement = entityElement.getFirstChildElement("titleInfo", ResolverConnector.RESOLVER_NAMESPACE);
        if(titleInfoElement != null) {
            Refiner.parseTitleInfoElement(titleInfoElement);
        }                
        Refiner.parseAndMatch(entityElement, "ccnb", "cnb\\d{9}|CNB\\d{9}");
        Refiner.parseAndMatch(entityElement, "isbn", "(978){0,1}80\\d([0-9]|){6}\\d[0-9xX]|(978-){0,1}80-\\d([0-9]|-){6}\\d-[0-9xX]|(978\\s){0,1}80\\s\\d([0-9]|\\s){6}\\d\\s[0-9xX]|978-80\\d([0-9]|){6}\\d[0-9xX]");
        Refiner.parseAndMatch(entityElement, "issn", "\\d{4}-\\d{3}[0-9Xx]{1}");                
        Refiner.parseAndCut(entityElement, "otherId", 50, 1);  
        Refiner.parseAndCut(entityElement, "documentType", 50, 0);
        //TODO:primaryOriginator
        Refiner.parseAndCut(entityElement, "otherOriginator", 50, 0);
        Refiner.parseAndMatch(entityElement, "digitalBorn", "true|false|0|1");  
        Refiner.parseAndCut(entityElement, "degreeAwardingInstitution", 50, 0);        
        
        Element publicationElement = entityElement.getFirstChildElement("publication", ResolverConnector.RESOLVER_NAMESPACE);
        if(publicationElement != null) {
            Refiner.parsePublicationElement(publicationElement);
        }            
        Element sourceDocumentElement = entityElement.getFirstChildElement("sourceDocument", ResolverConnector.RESOLVER_NAMESPACE);
        if(sourceDocumentElement != null) {
            parseEntityElement(sourceDocumentElement);
        }                  
    }
    
    private static void parseTitleInfoElement(Element titleInfoElement) {
        Refiner.parseAndCut(titleInfoElement, "title", 100, 1);  
        Refiner.parseAndCut(titleInfoElement, "subTitle", 200, 1);  
        Refiner.parseAndCut(titleInfoElement, "monographTitle", 100, 1);  
        Refiner.parseAndCut(titleInfoElement, "volumeTitle", 50, 0);  
        Refiner.parseAndCut(titleInfoElement, "periodicalTitle", 100, 1);
        Refiner.parseAndCut(titleInfoElement, "issueTitle", 50, 0);        
    }
    
    
    private static void parsePublicationElement(Element publicationElement) {
        Refiner.parseAndCut(publicationElement, "publisher", 50, 0);  
        Refiner.parseAndCut(publicationElement, "place", 50, 0);  
        Refiner.parseAndMatch(publicationElement, "year", "\\d{1,4}");        
    }
    
    private static void parseUrnNbnElement(Element urnNbnElement) {
        Refiner.parseAndMatch(urnNbnElement, "value", "urn:nbn:cz:[A-Za-z0-9]{2,6}\\-[A-Za-z0-9]{6}"); 
    }

    private static void parseDigitalDocumentElement(Element digitalDocumentElement) {
        Refiner.parseAndMatch(digitalDocumentElement, "archiverId", "\\d*");
        
        Element urnNbnElement = digitalDocumentElement.getFirstChildElement("urnNbn", ResolverConnector.RESOLVER_NAMESPACE);
        if(urnNbnElement != null) {
            Refiner.parseUrnNbnElement(urnNbnElement);
        }                    
        //Refiner.parseAndMatch(digitalDocumentElement, "urnNbn", "urn:nbn:cz:[A-Za-z0-9]{2,6}\\-[A-Za-z0-9]{6}"); 
        //TODO: registrarScopeIdentifiers
        Refiner.parseAndCut(digitalDocumentElement, "financed", 100, 1);          
        Element technicalMetadataElement = digitalDocumentElement.getFirstChildElement("technicalMetadata", ResolverConnector.RESOLVER_NAMESPACE);
        if(technicalMetadataElement != null) {
            parseTechnicalMetadataElement(technicalMetadataElement);
        }                    
    }

    
    private static void parseTechnicalMetadataElement(Element technicalMetadataElement) {
        Refiner.parseAndCut(technicalMetadataElement, "format", 20, 1);          
        Refiner.parseAndCut(technicalMetadataElement, "extent", 200, 1);          
        Element resolutionElement = technicalMetadataElement.getFirstChildElement("resolution", ResolverConnector.RESOLVER_NAMESPACE);
        if(resolutionElement != null) {
            if(parseResolutionElement(resolutionElement)) {
                technicalMetadataElement.removeChild(resolutionElement);
            }
        }            
        Refiner.parseAndCut(technicalMetadataElement, "compression", 50, 1);   
        Element colorElement = technicalMetadataElement.getFirstChildElement("color", ResolverConnector.RESOLVER_NAMESPACE);
        if(colorElement != null) {
            parseColorElement(colorElement);
        }           
        Refiner.parseAndCut(technicalMetadataElement, "iccProfile", 50, 1);   
        Element pictureSizenElement = technicalMetadataElement.getFirstChildElement("pictureSize", ResolverConnector.RESOLVER_NAMESPACE);
        if(pictureSizenElement != null) {
            if(parsePictureSizeElement(pictureSizenElement)) {
                technicalMetadataElement.removeChild(pictureSizenElement);
            }
        }                    
    }
    
    
    private static void parseColorElement(Element colorElement) {                        
        Refiner.parseAndCut(colorElement, "model", 20, 1);          
        Refiner.parseAndMatch(colorElement, "depth", "\\d*");        
    }    
    
    
    private static boolean parseResolutionElement(Element resolutionElement) {                        
        boolean hRemoved = Refiner.parseAndMatch(resolutionElement, "horizontal", "\\d*");
        boolean vRemoved = Refiner.parseAndMatch(resolutionElement, "vertical", "\\d*");
        return hRemoved || vRemoved;
    }    

    
    private static boolean parsePictureSizeElement(Element pictureSizenElement) {                        
        boolean wRemoved = Refiner.parseAndMatch(pictureSizenElement, "width", "\\d*");
        boolean hRemoved = Refiner.parseAndMatch(pictureSizenElement, "height", "\\d*");
        return wRemoved || hRemoved;
    }        
    
    
    private static void parseAndCut(Element parent, String name, int maxLength, int minLength) {
        Element el = parent.getFirstChildElement(name, ResolverConnector.RESOLVER_NAMESPACE);
        if(el != null) {
            String value = el.getValue();
            if(value.length() > maxLength) {
                el.removeChildren();
                el.appendChild(value.substring(0, maxLength));
            }
            if(value.length() < minLength) {
                parent.removeChild(el);
            }
        }
    }

    
    private static boolean parseAndMatch(Element parent, String name, String regex) {
        Element el = parent.getFirstChildElement(name, ResolverConnector.RESOLVER_NAMESPACE);
        if(el != null) {
            String value = el.getValue();
            if (!value.matches(regex)) {
                parent.removeChild(el);
                return true;
            }
        }
        return false;
    }    

    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter.utils;

import cz.nkp.urnnbn.oaiadapter.DocumentOperationException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

/**
 *
 * @author hanis
 */
public class Parser {
    
    public static final String NS = "http://resolver.nkp.cz/v2/";    

    private Parser() {    
    }
    
    public static void parseDocument(Document document) throws ImportParsingException {
        Element root = document.getRootElement();
        if("import".equals(root.getLocalName())) {
            Parser.parseImportElement(root);
        }                                    
        try {
            XmlTools.validateImport(document);
        } catch (DocumentOperationException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(document.toXML());        
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
                Parser.parseEntityElement(entityElement);            
            }
        }
        Element digitalDocumentElement = importElement.getFirstChildElement("digitalDocument", NS);
        if(digitalDocumentElement != null) {
            Parser.parseDigitalDocumentElement(digitalDocumentElement);
        }            
    }
    
    private static void parseEntityElement(Element entityElement) {        
        Element titleInfoElement = entityElement.getFirstChildElement("titleInfo", Parser.NS);
        if(titleInfoElement != null) {
            Parser.parseTitleInfoElement(titleInfoElement);
        }                
        Parser.parseAndMatch(entityElement, "ccnb", "cnb\\d{9}|CNB\\d{9}");
        Parser.parseAndMatch(entityElement, "isbn", "(978){0,1}80\\d([0-9]|){6}\\d[0-9xX]|(978-){0,1}80-\\d([0-9]|-){6}\\d-[0-9xX]|(978\\s){0,1}80\\s\\d([0-9]|\\s){6}\\d\\s[0-9xX]|978-80\\d([0-9]|){6}\\d[0-9xX]");
        Parser.parseAndMatch(entityElement, "issn", "\\d{4}-\\d{3}[0-9Xx]{1}");                
        Parser.parseAndCut(entityElement, "otherId", 50, 1);  
        Parser.parseAndCut(entityElement, "documentType", 50, 0);
        //TODO:primaryOriginator
        Parser.parseAndCut(entityElement, "otherOriginator", 50, 0);
        Parser.parseAndMatch(entityElement, "digitalBorn", "true|false|0|1");  
        Parser.parseAndCut(entityElement, "degreeAwardingInstitution", 50, 0);        
        
        Element publicationElement = entityElement.getFirstChildElement("publication", Parser.NS);
        if(publicationElement != null) {
            Parser.parsePublicationElement(publicationElement);
        }            
        Element sourceDocumentElement = entityElement.getFirstChildElement("sourceDocument", Parser.NS);
        if(sourceDocumentElement != null) {
            parseEntityElement(sourceDocumentElement);
        }                  
    }
    
    private static void parseTitleInfoElement(Element titleInfoElement) {
        Parser.parseAndCut(titleInfoElement, "title", 100, 1);  
        Parser.parseAndCut(titleInfoElement, "subTitle", 200, 1);  
        Parser.parseAndCut(titleInfoElement, "monographTitle", 100, 1);  
        Parser.parseAndCut(titleInfoElement, "volumeTitle", 50, 0);  
        Parser.parseAndCut(titleInfoElement, "periodicalTitle", 100, 1);
        Parser.parseAndCut(titleInfoElement, "issueTitle", 50, 0);        
    }
    
    
    private static void parsePublicationElement(Element publicationElement) {
        Parser.parseAndCut(publicationElement, "publisher", 50, 0);  
        Parser.parseAndCut(publicationElement, "place", 50, 0);  
        Parser.parseAndMatch(publicationElement, "year", "\\d{1,4}");        
    }
    

    private static void parseDigitalDocumentElement(Element digitalDocumentElement) {
        Parser.parseAndMatch(digitalDocumentElement, "archiverId", "\\d*");
        Parser.parseAndMatch(digitalDocumentElement, "urnNbn", "urn:nbn:cz:[A-Za-z0-9]{2,6}\\-[A-Za-z0-9]{6}"); 
        //TODO: registrarScopeIdentifiers
        Parser.parseAndCut(digitalDocumentElement, "financed", 100, 1);          
        Element technicalMetadataElement = digitalDocumentElement.getFirstChildElement("technicalMetadata", Parser.NS);
        if(technicalMetadataElement != null) {
            parseTechnicalMetadataElement(technicalMetadataElement);
        }                    
    }

    
    private static void parseTechnicalMetadataElement(Element technicalMetadataElement) {
        Parser.parseAndCut(technicalMetadataElement, "format", 20, 1);          
        Parser.parseAndCut(technicalMetadataElement, "extent", 200, 1);          
        Element resolutionElement = technicalMetadataElement.getFirstChildElement("resolution", Parser.NS);
        if(resolutionElement != null) {
            if(parseResolutionElement(resolutionElement)) {
                technicalMetadataElement.removeChild(resolutionElement);
            }
        }            
        Parser.parseAndCut(technicalMetadataElement, "compression", 50, 1);   
        Element colorElement = technicalMetadataElement.getFirstChildElement("color", Parser.NS);
        if(colorElement != null) {
            parseColorElement(colorElement);
        }           
        Parser.parseAndCut(technicalMetadataElement, "iccProfile", 50, 1);   
        Element pictureSizenElement = technicalMetadataElement.getFirstChildElement("pictureSize", Parser.NS);
        if(pictureSizenElement != null) {
            if(parsePictureSizeElement(pictureSizenElement)) {
                technicalMetadataElement.removeChild(pictureSizenElement);
            }
        }                    
    }
    
    
    private static void parseColorElement(Element colorElement) {                        
        Parser.parseAndCut(colorElement, "model", 20, 1);          
        Parser.parseAndMatch(colorElement, "depth", "\\d*");        
    }    
    
    
    private static boolean parseResolutionElement(Element resolutionElement) {                        
        boolean hRemoved = Parser.parseAndMatch(resolutionElement, "horizontal", "\\d*");
        boolean vRemoved = Parser.parseAndMatch(resolutionElement, "vertical", "\\d*");
        return hRemoved || vRemoved;
    }    

    
    private static boolean parsePictureSizeElement(Element pictureSizenElement) {                        
        boolean wRemoved = Parser.parseAndMatch(pictureSizenElement, "width", "\\d*");
        boolean hRemoved = Parser.parseAndMatch(pictureSizenElement, "height", "\\d*");
        return wRemoved || hRemoved;
    }        
    
    
    private static void parseAndCut(Element parent, String name, int maxLength, int minLength) {
        Element el = parent.getFirstChildElement(name, Parser.NS);
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
        Element el = parent.getFirstChildElement(name, Parser.NS);
        if(el != null) {
            String value = el.getValue();
            if (!value.matches(regex)) {
                parent.removeChild(el);
                return true;
            }
        }
        return false;
    }    

    
    public static void main(String[] args) {
        File file = new File("/home/hanis/prace/resolver/oai/parser-test/thesis.xml");
        Builder builder = new Builder();
        Document doc = null;
        try {
            doc = builder.build(file);
        } catch (ParsingException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        }                                
        try {
            Parser.parseDocument(doc);
        } catch (ImportParsingException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}

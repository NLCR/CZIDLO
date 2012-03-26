/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.legacyrecordsimport.validation;

import nu.xom.ValidityException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Jan Rychtář
 */
public class ImportErrorHandler implements ErrorHandler {

    public void warning(SAXParseException e) throws SAXException {
        System.out.println(e.getMessage());
    }

    public void error(SAXParseException e) throws SAXException {
        System.out.println(e.getMessage());        
    }

    public void fatalError(SAXParseException e) throws SAXException {
        System.out.println(e.getMessage());        
    }
    
}

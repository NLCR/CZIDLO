/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.legacyrecordsimport;

/**
 *
 * @author Martin Řehánek
 */
public class DatabaseException extends Exception{

    DatabaseException(Exception ex) {
        super(ex);
    }
    
}

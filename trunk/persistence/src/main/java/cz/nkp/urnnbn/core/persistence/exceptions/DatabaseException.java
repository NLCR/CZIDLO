/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.exceptions;

/**
 *
 * @author Martin Řehánek
 */
public class DatabaseException extends Exception {

    public DatabaseException(Exception ex) {
        super(ex);
    }
}

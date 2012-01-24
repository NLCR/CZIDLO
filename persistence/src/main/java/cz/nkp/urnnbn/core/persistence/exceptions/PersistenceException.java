/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.nkp.urnnbn.core.persistence.exceptions;

/**
 *
 * @author Martin Řehánek
 */
public class PersistenceException extends Exception {

    /**
     * Creates a new instance of <code>PersistenceException</code> without detail message.
     */
    public PersistenceException() {
    }


    /**
     * Constructs an instance of <code>PersistenceException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PersistenceException(String msg) {
        super(msg);
    }
}

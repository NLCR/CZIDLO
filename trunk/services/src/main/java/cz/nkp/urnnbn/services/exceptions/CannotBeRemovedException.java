/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

/**
 *
 * @author Martin Řehánek
 */
public class CannotBeRemovedException extends Exception {

    /**
     * Creates a new instance of <code>CannotBeRemovedException</code> without detail message.
     */
    public CannotBeRemovedException() {
    }

    /**
     * Constructs an instance of <code>CannotBeRemovedException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public CannotBeRemovedException(String msg) {
        super(msg);
    }
}

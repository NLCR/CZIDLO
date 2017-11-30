/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.legacyrecordsimport;

/**
 *
 * @author hanis
 */
public class RegistrarCodeNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>RegistrarCodeNotFoundException</code> without detail message.
     */
    public RegistrarCodeNotFoundException() {
    }

    /**
     * Constructs an instance of <code>RegistrarCodeNotFoundException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public RegistrarCodeNotFoundException(String msg) {
        super(msg);
    }
}

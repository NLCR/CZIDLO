/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.legacyrecordsimport;

/**
 *
 * @author hanis
 */
public class SiglaNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>SiglaNotFoundException</code> without detail message.
     */
    public SiglaNotFoundException() {
    }

    /**
     * Constructs an instance of <code>SiglaNotFoundException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SiglaNotFoundException(String msg) {
        super(msg);
    }
}

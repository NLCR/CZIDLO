/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.nkp.urnnbn.core.persistence.exceptions;

/**
 *
 * @author Martin Řehánek
 */
public class MultipleRecordsException extends PersistenceException {

    /**
     * Creates a new instance of <code>MultipleRecordsException</code> without detail message.
     */
    public MultipleRecordsException() {
    }

    /**
     * Constructs an instance of <code>MultipleRecordsException</code> with the specified detail message.
     * 
     * @param msg
     *            the detail message.
     */
    public MultipleRecordsException(String msg) {
        super(msg);
    }
}

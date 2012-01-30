/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

/**
 *
 * @author Martin Řehánek
 */
public class UnknownDigiLibException extends Exception {

    public UnknownDigiLibException(long libraryId) {
        super("unknown digital library with id: " + libraryId);
    }
}

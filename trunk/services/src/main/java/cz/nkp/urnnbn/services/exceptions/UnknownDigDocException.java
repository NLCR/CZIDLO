/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

/**
 *
 * @author Martin Řehánek
 */
public class UnknownDigDocException extends Exception {

    public UnknownDigDocException(long digDocId) {
        super("unknown digital document with internal id: " + digDocId);
    }
}

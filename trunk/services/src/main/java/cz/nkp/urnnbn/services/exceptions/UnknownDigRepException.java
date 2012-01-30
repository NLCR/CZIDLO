/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

/**
 *
 * @author Martin Řehánek
 */
public class UnknownDigRepException extends Exception {

    public UnknownDigRepException(long digRepId) {
        super("unknown digital representation with id: " + digRepId);
    }
}

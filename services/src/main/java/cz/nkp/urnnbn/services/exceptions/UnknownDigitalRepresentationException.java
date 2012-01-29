/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

/**
 *
 * @author Martin Řehánek
 */
public class UnknownDigitalRepresentationException extends Exception {

    public UnknownDigitalRepresentationException(long digRepId) {
        super("unknown digital representation with id: " + digRepId);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

/**
 *
 * @author Martin Řehánek
 */
public class UnknownDigInstException extends Exception {

    public UnknownDigInstException(long digInstId) {
        super("unknown digital instance with id: " + digInstId);
    }
}

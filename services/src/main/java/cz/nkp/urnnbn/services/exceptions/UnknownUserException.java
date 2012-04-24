/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

/**
 *
 * @author Martin Řehánek
 */
public class UnknownUserException extends Exception {

    public UnknownUserException(String login) {
        super("unknown user with login " + login);
    }

    public UnknownUserException(Long userId) {
        super("unknown user with id " + userId);
    }
}

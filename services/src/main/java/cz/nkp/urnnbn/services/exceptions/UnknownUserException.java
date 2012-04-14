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

    private final String login;

    public UnknownUserException(String login) {
        super("unknown user with login " + login);
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}

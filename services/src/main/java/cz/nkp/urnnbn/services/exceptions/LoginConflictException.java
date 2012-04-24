/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

/**
 *
 * @author Martin Řehánek
 */
public class LoginConflictException extends Exception {

    public LoginConflictException(String login) {
        super("user with login " + login + " already exists");
    }
}

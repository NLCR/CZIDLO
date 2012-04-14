/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

/**
 *
 * @author Martin Řehánek
 */
public class NotAdminException extends Exception {

    public NotAdminException(long userId) {
        super("user with id " + userId + " does not have admin rights");
    }

    public NotAdminException(String login) {
        super("user " + login + " does not have admin rights");
    }
}

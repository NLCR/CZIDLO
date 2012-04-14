/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

import cz.nkp.urnnbn.core.RegistrarCode;
    
/**
 *
 * @author Martin Řehánek
 */
public class AccessException extends Exception {

    private final String login;

    public AccessException(String login, RegistrarCode registrarCode) {
        super("user " + login + " not allowed to manage registrar with code " + registrarCode);
        this.login = login;
    }

    public AccessException(String login, long registrarId) {
        super("user " + login + " not allowed to manage registrar with id " + registrarId);
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}

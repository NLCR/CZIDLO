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

    public AccessException(long userId, RegistrarCode registrarCode) {
        super("user with id " + userId + " not allowed to manage registrar with code " + registrarCode);
    }

    public AccessException(long userId, long registrarId) {
        super("user with id " + userId + " not allowed to manage registrar with id " + registrarId);
    }
}

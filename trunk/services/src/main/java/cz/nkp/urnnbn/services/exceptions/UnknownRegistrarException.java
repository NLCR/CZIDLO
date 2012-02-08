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
public class UnknownRegistrarException extends Exception {

    public UnknownRegistrarException(RegistrarCode code) {
        super("unknown registrar with code " + code);
    }

    public UnknownRegistrarException(long registrarId) {
        super("unknown registrar with id: " + registrarId);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarCollisionException extends Exception {

    private final String registrarCode;

    public RegistrarCollisionException(String registrarCode) {
        super("registrar with code " + registrarCode + " already exists");
        this.registrarCode = registrarCode;
    }

    public String getRegistrarCode() {
        return registrarCode;
    }
}

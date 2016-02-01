/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.exceptions;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarRightCollisionException extends Exception {

    public RegistrarRightCollisionException(Long userId, Long registarId) {
        super("user " + userId + " already has access right to registar " + registarId);
    }
}

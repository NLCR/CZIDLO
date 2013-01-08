/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.exceptions;

import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Martin Řehánek
 */
public class InvalidRegistrarCodeException extends ApiException {

    public InvalidRegistrarCodeException(String registarCode, String message) {
        super(Status.BAD_REQUEST, "INVALID_REGISTRAR_CODE", "Incorrect syntax in '" + registarCode + "': " + message);
    }
}

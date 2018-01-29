/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3.exceptions;

import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Martin Řehánek
 */
public class InvalidRegistrarCodeException extends ApiV3Exception {

    public InvalidRegistrarCodeException(String registrarCode, String message) {
        super(Status.BAD_REQUEST, "INVALID_REGISTRAR_CODE", "Incorrect syntax in '" + registrarCode + "': " + message);
    }
}

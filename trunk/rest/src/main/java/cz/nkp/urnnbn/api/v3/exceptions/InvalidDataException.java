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
public class InvalidDataException extends ApiV3Exception {

    public InvalidDataException(String message) {
        super(Status.BAD_REQUEST, "INVALID_DATA", message);
    }

    public InvalidDataException(Exception ex) {
        super(Status.BAD_REQUEST, "INVALID_DATA", ex.getMessage());
    }
}

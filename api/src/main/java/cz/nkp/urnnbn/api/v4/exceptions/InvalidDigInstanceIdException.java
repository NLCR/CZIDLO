/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v4.exceptions;

import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Martin Řehánek
 */
public class InvalidDigInstanceIdException extends ApiV4Exception {

    public InvalidDigInstanceIdException(String id, String message) {
        super(Status.BAD_REQUEST, "INVALID_DIGITAL_INSTANCE_ID", "Incorrect syntax in '" + id + "': " + message);
    }
}

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
public class InvalidArchiverIdException extends ApiV3Exception {

    public InvalidArchiverIdException(String errorMessage) {
        super(Status.BAD_REQUEST, "INVALID_ARCHIVER_ID", errorMessage);
    }
}

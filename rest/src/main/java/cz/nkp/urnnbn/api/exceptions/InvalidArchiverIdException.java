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
public class InvalidArchiverIdException extends ApiException {

    public InvalidArchiverIdException(String errorMessage) {
        super(Status.BAD_REQUEST, "INVALID_ARCHIVER_ID", errorMessage);
    }
}
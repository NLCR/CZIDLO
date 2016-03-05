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
public class IncorrectDigitalLibraryException extends ApiV4Exception {

    public IncorrectDigitalLibraryException(String message) {
        super(Status.BAD_REQUEST, "INCORRECT_DIGITAL_LIBRARY", message);
    }
}

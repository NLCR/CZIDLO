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
public class UnknownDigitalLibraryException extends ApiV3Exception {

    public UnknownDigitalLibraryException(String errorMessage) {
        super(Status.NOT_FOUND, "UNKNOWN_DIGITAL_LIBRARY", errorMessage);
    }
}

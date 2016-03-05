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
public class InvalidUrnException extends ApiV4Exception {

    public InvalidUrnException(String urnString, String errorMessage) {
        super(Status.BAD_REQUEST, "INVALID_URN_NBN", urnString + ": " + errorMessage);
    }
}

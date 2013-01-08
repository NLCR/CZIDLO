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
public class InvalidUrnException extends ApiException {

    public InvalidUrnException(String urnString, String errorMessage) {
        super(Status.BAD_REQUEST, "INVALID_URN_NBN", urnString + ": " + errorMessage);
    }
}

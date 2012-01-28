/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest.exceptions;

import javax.ws.rs.core.Response.Status;

/**
 * 
 * @author Martin Řehánek
 */
public class InvalidDigRepIdType extends RestException {

    public InvalidDigRepIdType(String stringValue, String errorMessage) {
        super(Status.BAD_REQUEST, "INVALID_DIGITAL_REPRESENTATION_ID_TYPE", "Incorrect syntax in '" + stringValue + "':" + errorMessage);
    }
}

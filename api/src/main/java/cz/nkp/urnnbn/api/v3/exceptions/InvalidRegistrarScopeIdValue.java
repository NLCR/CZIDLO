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
public class InvalidRegistrarScopeIdValue extends ApiV3Exception {

    public InvalidRegistrarScopeIdValue(String stringValue, String errorMessage) {
        super(Status.BAD_REQUEST, "INVALID_DIGITAL_DOCUMENT_ID_VALUE", String.format("Incorrect value '%s': %s", stringValue, errorMessage));
    }
}

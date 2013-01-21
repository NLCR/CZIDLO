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
public class InvalidRegistrarScopeIdentifier extends ApiV3Exception {

    public InvalidRegistrarScopeIdentifier(String errorMessage) {
        super(Status.BAD_REQUEST, "INVALID_REGISTRAR_SCOPE_IDENTIFIER", errorMessage);
    }
}

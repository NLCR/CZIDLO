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
public class NotAuthorizedException extends ApiV3Exception {

    public NotAuthorizedException(String errorMessage) {
        super(Status.UNAUTHORIZED, "NOT_AUTHORIZED", errorMessage);
    }
}

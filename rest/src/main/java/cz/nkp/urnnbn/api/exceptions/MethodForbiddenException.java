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
public class MethodForbiddenException extends ApiException {

    public MethodForbiddenException() {
        super(Status.FORBIDDEN, "FORBIDDEN", "This method is (temporarily) not allowed");
    }
}

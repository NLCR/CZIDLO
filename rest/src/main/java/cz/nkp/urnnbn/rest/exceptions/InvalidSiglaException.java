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
public class InvalidSiglaException extends RestException {

    public InvalidSiglaException(String sigla) {
        super(Status.BAD_REQUEST, "INVALID_SIGLA", "Incorrect syntax in '" + sigla + "'");
    }
}

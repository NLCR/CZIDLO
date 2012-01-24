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
public class InvalidDataException extends RestException {

    public InvalidDataException(String message) {
        super(Status.BAD_REQUEST, "INVALID_DATA", message);
    }
 }

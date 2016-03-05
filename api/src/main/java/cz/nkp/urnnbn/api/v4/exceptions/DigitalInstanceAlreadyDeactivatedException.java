/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v4.exceptions;

import javax.ws.rs.core.Response;

import cz.nkp.urnnbn.core.dto.DigitalInstance;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalInstanceAlreadyDeactivatedException extends ApiV4Exception {

    public DigitalInstanceAlreadyDeactivatedException(DigitalInstance instance) {
        super(Response.Status.FORBIDDEN, "ALREADY_DEACTIVATED", "Digital instance with id " + instance.getId() + " has already been deactivated ("
                + instance.getDeactivated().toString() + ")");
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest.exceptions;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import javax.ws.rs.core.Response;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalInstanceAlreadyDeactivated extends RestException {

    public DigitalInstanceAlreadyDeactivated(DigitalInstance instance) {
        super(Response.Status.FORBIDDEN, "ALREADY_DEACTIVATED",
                "Digital instance with id " + instance.getId()
                + " has already been deactivated (" + instance.getModified().toString() + ")");
    }
}

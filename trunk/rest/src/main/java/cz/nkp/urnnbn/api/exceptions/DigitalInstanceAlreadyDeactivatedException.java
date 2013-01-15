/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.exceptions;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import javax.ws.rs.core.Response;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalInstanceAlreadyDeactivatedException extends ApiException {

    public DigitalInstanceAlreadyDeactivatedException(DigitalInstance instance) {
        super(Response.Status.FORBIDDEN, "ALREADY_DEACTIVATED",
                "Digital instance with id " + instance.getId()
                + " has already been deactivated (" + instance.getDeactivated().toString() + ")");
    }
}

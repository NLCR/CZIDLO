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
public class DigitalInstanceAlreadyPresentException extends ApiException {

    public DigitalInstanceAlreadyPresentException(DigitalInstance instance) {
        super(Response.Status.FORBIDDEN, "DIGITAL_INSTANCE_ALREADY_PRESENT",
                "Digital instance for this digital document is already present in digital library with id=" + instance.getLibraryId()
                + " (digital instance with id=" + instance.getId() + ")");
    }
}

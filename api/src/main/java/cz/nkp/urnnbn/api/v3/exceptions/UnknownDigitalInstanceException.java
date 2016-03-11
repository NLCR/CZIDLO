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
public class UnknownDigitalInstanceException extends ApiV3Exception {

    public UnknownDigitalInstanceException(long id) {
        super(Status.NOT_FOUND, "UNKNOWN_DIGITAL_INSTANCE", "No digital instance with id " + id);
    }

    public UnknownDigitalInstanceException() {
        super(Status.NOT_FOUND, "UNKNOWN_DIGITAL_INSTANCE", "No active digital instance found");
    }
}

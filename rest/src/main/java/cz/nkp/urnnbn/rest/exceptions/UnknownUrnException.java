/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest.exceptions;

import cz.nkp.urnnbn.core.dto.UrnNbn;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Martin Řehánek
 */
public class UnknownUrnException extends ApiException {

    public UnknownUrnException(UrnNbn urn) {
        super(Status.NOT_FOUND, "UNKNOWN_URN", "No such urn '" + urn.toString() + "' has been assigned yet");
    }
}

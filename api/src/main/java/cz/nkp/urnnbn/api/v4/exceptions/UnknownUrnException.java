/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v4.exceptions;

import javax.ws.rs.core.Response.Status;

import cz.nkp.urnnbn.core.dto.UrnNbn;

/**
 *
 * @author Martin Řehánek
 */
public class UnknownUrnException extends ApiV4Exception {

    public UnknownUrnException(UrnNbn urn) {
        super(Status.NOT_FOUND, "UNKNOWN_URN", "No such urn '" + urn.toString() + "' has been assigned yet");
    }
}

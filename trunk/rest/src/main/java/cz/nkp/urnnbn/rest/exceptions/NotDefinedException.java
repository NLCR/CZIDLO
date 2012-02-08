/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest.exceptions;

import cz.nkp.urnnbn.core.DigDocIdType;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Martin Řehánek
 */
public class NotDefinedException extends RestException {

    public NotDefinedException(DigDocIdType idType) {
        super(Status.NOT_FOUND, "NOT_DEFINED", "No value defined for identifier of type '" + idType.toString() + "'");
    }
}

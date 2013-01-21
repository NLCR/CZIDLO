/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3.exceptions;

import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Martin Řehánek
 */
public class NotDefinedException extends ApiV3Exception {

    public NotDefinedException(RegistrarScopeIdType idType) {
        super(Status.NOT_FOUND, "NOT_DEFINED", "No value defined for identifier of type '" + idType.toString() + "'");
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest.exceptions;

import cz.nkp.urnnbn.core.RegistrarCode;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Martin Řehánek
 */
public class UnknownRegistrarException extends ApiException {

    public UnknownRegistrarException(RegistrarCode sigla) {
        super(Status.NOT_FOUND, "UNKNOWN_REGISTRAR", "There is no registrar with sigla '" + sigla + "'");
    }
}

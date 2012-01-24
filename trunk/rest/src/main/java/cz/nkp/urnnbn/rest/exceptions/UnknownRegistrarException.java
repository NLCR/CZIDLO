/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest.exceptions;

import cz.nkp.urnnbn.core.Sigla;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Martin Řehánek
 */
public class UnknownRegistrarException extends RestException {

    public UnknownRegistrarException(Sigla sigla) {
        super(Status.NOT_FOUND, "UNKNOWN_REGISTRAR", "There is no registrar with sigla '" + sigla + "'");
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3.exceptions;

import javax.ws.rs.core.Response.Status;

import cz.nkp.urnnbn.core.RegistrarCode;

/**
 *
 * @author Martin Řehánek
 */
public class UnknownRegistrarException extends ApiV3Exception {

    public UnknownRegistrarException(RegistrarCode sigla) {
        super(Status.NOT_FOUND, "UNKNOWN_REGISTRAR", "There is no registrar with sigla '" + sigla + "'");
    }
}

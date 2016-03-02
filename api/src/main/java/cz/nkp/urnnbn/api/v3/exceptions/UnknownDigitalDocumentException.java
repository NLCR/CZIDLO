/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3.exceptions;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.RegistrarScopeIdValue;
import cz.nkp.urnnbn.core.dto.UrnNbn;

import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Martin Řehánek
 */
public class UnknownDigitalDocumentException extends ApiV3Exception {

    private static final String errorCode = "UNKNOWN_DIGITAL_DOCUMENT";

    public UnknownDigitalDocumentException(UrnNbn urn) {
        super(Status.NOT_FOUND, errorCode, "There is no digital document with urn '" + urn + "'");
    }

    public UnknownDigitalDocumentException(long digDocId) {
        super(Status.NOT_FOUND, errorCode, "There is no digital document with internal id '" + digDocId + "'");
    }

    public UnknownDigitalDocumentException(RegistrarCode registrarCode, RegistrarScopeIdType idType, RegistrarScopeIdValue idValue) {
        super(Status.NOT_FOUND, errorCode, String.format(
                "Registrar with code '%s' doesn't register digital document with identifier of type '%s' and value '%s'",//
                registrarCode, idType.toString(), idValue.toString()));
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3.exceptions;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Martin Řehánek
 */
public class UnknownDigitalDocumentException extends ApiV3Exception {

    private static final String errorCode = "UNKNOWN_DIGITAL_DOCUMENT";

    public UnknownDigitalDocumentException(UrnNbn urn) {
        super(Status.NOT_FOUND, errorCode,
                "There is no digital document with urn '" + urn + "'");
    }

    public UnknownDigitalDocumentException(long digDocId) {
        super(Status.NOT_FOUND, errorCode,
                "There is no digital document with internal id '" + digDocId + "'");
    }

    public UnknownDigitalDocumentException(RegistrarCode code, RegistrarScopeIdType idType, String idValue) {
        super(Status.NOT_FOUND, errorCode,
                "Registrar with code " + code
                + " doesn't register digital document with identifier of type '" + idType.toString()
                + "' and value '" + idValue + "'");
    }
}
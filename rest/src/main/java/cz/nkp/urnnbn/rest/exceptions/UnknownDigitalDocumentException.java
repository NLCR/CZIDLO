/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest.exceptions;

import cz.nkp.urnnbn.core.DigDocIdType;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Martin Řehánek
 */
public class UnknownDigitalDocumentException extends RestException {

    private static final String errorCode = "UNKNOWN_DIGITAL_REPRESENTATION";

    public UnknownDigitalDocumentException(UrnNbn urn) {
        super(Status.NOT_FOUND, errorCode,
                "There is no digital representation with urn '" + urn + "'");
    }

    public UnknownDigitalDocumentException(long digRepId) {
        super(Status.NOT_FOUND, errorCode,
                "There is no digital representation with id: '" + digRepId + "'");
    }

    public UnknownDigitalDocumentException(String sigla, DigDocIdType idType, String idValue) {
        super(Status.NOT_FOUND, errorCode,
                "Registrar with sigla " + sigla
                + " doesn't register digital representation with identifier of type '" + idType.toString()
                + "' and value '" + idValue + "'");
    }
}

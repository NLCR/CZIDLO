/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.nkp.urnnbn.api.v6.exceptions;

import cz.nkp.urnnbn.api.v6.ResponseFormat;
import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.RegistrarScopeIdValue;
import cz.nkp.urnnbn.core.dto.UrnNbn;

import javax.ws.rs.core.Response.Status;

public class UnknownDigitalDocumentException extends ApiV6Exception {

    private static final String errorCode = "UNKNOWN_DIGITAL_DOCUMENT";

    public UnknownDigitalDocumentException(ResponseFormat format, UrnNbn urn) {
        super(format, Status.NOT_FOUND, errorCode, String.format("There is no digital document registered with '%s'.", urn));
    }

    public UnknownDigitalDocumentException(ResponseFormat format, long digDocId) {
        super(format, Status.NOT_FOUND, errorCode, String.format("There is no digital document registered with internal id '%d'.", digDocId));
    }

    public UnknownDigitalDocumentException(ResponseFormat format, RegistrarCode registrarCode,
                                           RegistrarScopeIdType idType, RegistrarScopeIdValue idValue) {
        super(format, Status.NOT_FOUND, errorCode, String.format(
                "Registrar with code '%s' does not register digital document with identifier with type '%s' and value '%s'",//
                registrarCode, idType.toString(), idValue.toString()));
    }

    public UnknownDigitalDocumentException(ResponseFormat format, String id) {
        super(format, Status.NOT_FOUND, errorCode, String.format("There is no digital document registered with '%s'.", id));
    }

}

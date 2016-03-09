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
package cz.nkp.urnnbn.api.v4.exceptions;

import javax.ws.rs.core.Response.Status;

import cz.nkp.urnnbn.api.v4.ResponseFormat;
import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.RegistrarScopeIdValue;
import cz.nkp.urnnbn.core.dto.UrnNbn;

public class UnknownDigitalDocumentException extends ApiV4Exception {

    private static final String errorCode = "UNKNOWN_DIGITAL_DOCUMENT";

    public UnknownDigitalDocumentException(ResponseFormat format, UrnNbn urn) {
        super(format, Status.NOT_FOUND, errorCode, "There is no digital document with urn '" + urn + "'");
    }

    public UnknownDigitalDocumentException(ResponseFormat format, long digDocId) {
        super(format, Status.NOT_FOUND, errorCode, "There is no digital document with internal id '" + digDocId + "'");
    }

    public UnknownDigitalDocumentException(ResponseFormat format, RegistrarCode registrarCode, RegistrarScopeIdType idType,
            RegistrarScopeIdValue idValue) {
        super(format, Status.NOT_FOUND, errorCode, String.format(
                "Registrar with code '%s' doesn't register digital document with identifier of type '%s' with value '%s'",//
                registrarCode, idType.toString(), idValue.toString()));
    }

}

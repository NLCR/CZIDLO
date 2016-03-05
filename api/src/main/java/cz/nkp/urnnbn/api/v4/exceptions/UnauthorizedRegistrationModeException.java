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

import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;

public class UnauthorizedRegistrationModeException extends ApiV4Exception {

    public UnauthorizedRegistrationModeException(UrnNbnRegistrationMode mode, UrnNbn urn, Registrar registrar) {
        super(Status.BAD_REQUEST, "UNAUTHORIZED_REGISTRATION_MODE", errorMessage(mode, urn, registrar));
    }

    private static String errorMessage(UrnNbnRegistrationMode mode, UrnNbn urn, Registrar registrar) {
        StringBuilder result = new StringBuilder();
        result.append("cannot register digital document ");
        if (urn != null) {
            result.append("with ").append(urn.toString()).append(' ');
        }
        result.append("- registration mode ").append(mode.toString()).append(' ');
        result.append("not allowed for registrar ").append(registrar.getCode()).append(" (").append(registrar.getName()).append(")");
        return result.toString();
    }
}

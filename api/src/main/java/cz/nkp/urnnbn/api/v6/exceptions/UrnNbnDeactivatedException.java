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
import cz.nkp.urnnbn.core.dto.UrnNbn;

import javax.ws.rs.core.Response;

public class UrnNbnDeactivatedException extends ApiV6Exception {

    public UrnNbnDeactivatedException(ResponseFormat format, UrnNbn urn) {
        super(format, Response.Status.FORBIDDEN, "URN_NBN_DEACTIVATED",
                String.format("The '%s' has been deactivated at %s.", urn, urn.getDeactivated().toString())
        );
    }
}

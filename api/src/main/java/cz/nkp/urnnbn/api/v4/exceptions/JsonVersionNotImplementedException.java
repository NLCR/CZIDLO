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

public class JsonVersionNotImplementedException extends ApiV4Exception {

    public JsonVersionNotImplementedException(ResponseFormat format) {
        // TODO: remove after all operations implemented
        super(format, Status.BAD_REQUEST, "NOT_IMPLEMENTED", "JSON version not implemented yet, try with format=xml");
    }

}

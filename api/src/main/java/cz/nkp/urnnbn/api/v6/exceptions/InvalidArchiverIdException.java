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

import javax.ws.rs.core.Response.Status;

public class InvalidArchiverIdException extends ApiV6Exception {

    public InvalidArchiverIdException(ResponseFormat format, String errorMessage) {
        super(format, Status.BAD_REQUEST, "INVALID_ARCHIVER_ID", errorMessage);
    }
}

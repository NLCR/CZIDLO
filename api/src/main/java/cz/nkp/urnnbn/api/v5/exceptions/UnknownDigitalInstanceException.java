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
package cz.nkp.urnnbn.api.v5.exceptions;

import cz.nkp.urnnbn.api.v5.ResponseFormat;

import javax.ws.rs.core.Response.Status;

public class UnknownDigitalInstanceException extends ApiV5Exception {

    public UnknownDigitalInstanceException(ResponseFormat format, long id) {
        super(format, Status.NOT_FOUND, "UNKNOWN_DIGITAL_INSTANCE", "No such digital instance with id " + id);
    }

    public UnknownDigitalInstanceException(ResponseFormat format) {
        super(format, Status.NOT_FOUND, "UNKNOWN_DIGITAL_INSTANCE", "No active digital instance found");
    }
}
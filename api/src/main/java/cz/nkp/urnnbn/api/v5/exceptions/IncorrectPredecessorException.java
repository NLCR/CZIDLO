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
import cz.nkp.urnnbn.core.UrnNbnWithStatus;

import javax.ws.rs.core.Response.Status;

public class IncorrectPredecessorException extends ApiV5Exception {

    public IncorrectPredecessorException(ResponseFormat format, UrnNbnWithStatus urn) {
        super(format, Status.BAD_REQUEST, getErrorCode(urn), urn.getUrn().toString());
    }

    private static String getErrorCode(UrnNbnWithStatus urn) {
        if (urn.getStatus() == UrnNbnWithStatus.Status.RESERVED) {
            return "INCORRECT_PREDECESSOR_RESERVED";
        } else if (urn.getStatus() == UrnNbnWithStatus.Status.FREE) {
            return "INCORRECT_PREDECESSOR_FREE";
        } else {
            return "";
        }
    }
}

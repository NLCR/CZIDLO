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

public class IncorrectUrnStateException extends ApiV5Exception {

    private static final long serialVersionUID = -3179637427845166321L;

    public IncorrectUrnStateException(ResponseFormat format, UrnNbnWithStatus urnNbnWithStatus) {
        super(format, Status.FORBIDDEN, "INCORRECT_URN_NBN_STATE", urnNbnWithStatus.getUrn().toString() + ": "
                + urnNbnWithStatus.getStatus().toString());
    }
}

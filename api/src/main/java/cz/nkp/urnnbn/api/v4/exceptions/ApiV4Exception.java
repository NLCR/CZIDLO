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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import cz.nkp.urnnbn.api.v4.ResponseFormat;
import cz.nkp.urnnbn.api.v4.json.JsonErrorBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.XmlErrorBuilder;

public class ApiV4Exception extends WebApplicationException {

    public ApiV4Exception(ResponseFormat format, Status status, String errorCode, String errorMessage) {
        super(buildResponse(format, status, errorCode, errorMessage));
    }

    private static Response buildResponse(ResponseFormat format, Status status, String errorCode, String errorMessage) {
        switch (format) {
        case XML: {
            ResponseBuilder builder = Response.status(status);
            builder.entity(new XmlErrorBuilder(errorCode, errorMessage).buildDocumentWithResponseHeader().toXML());
            builder.type(MediaType.APPLICATION_XML);
            return builder.build();
        }
        case JSON: {
            ResponseBuilder builder = Response.status(status);
            builder.entity(new JsonErrorBuilder(errorCode, errorMessage).toJson());
            builder.type(MediaType.APPLICATION_JSON);
            return builder.build();
        }
        default: {
            throw new RuntimeException();
        }
        }
    }

}

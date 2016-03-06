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

import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import cz.nkp.urnnbn.api.v4.ResponseFormat;
import cz.nkp.urnnbn.xml.apiv4.builders.ErrorResponseBuilder;

public class ApiV4Exception extends WebApplicationException {

    private static final Logger LOGGER = Logger.getLogger(ApiV4Exception.class.getName());

    // private final ResponseFormat format;
    // private final Status status;
    // private final String errorCode;
    // private final String errorMessage;

    public ApiV4Exception(ResponseFormat format, Status status, String errorCode, String errorMessage) {
        super(buildResponse(format, status, errorCode, errorMessage));
        // this.status = status;
        // this.errorCode = errorCode;
        // this.errorMessage = errorMessage;
    }

    private static Response buildResponse(ResponseFormat format, Status status, String errorCode, String errorMessage) {
        switch (format) {
        case XML: {
            ResponseBuilder builder = Response.status(status);
            builder.entity(buildEntityXml(errorCode, errorMessage));
            builder.type(MediaType.TEXT_XML);
            return builder.build();
        }
        case JSON: {
            ResponseBuilder builder = Response.status(status);
            builder.entity(buildEntityJson(errorCode, errorMessage));
            builder.type(MediaType.APPLICATION_JSON);
            return builder.build();
        }
        default: {
            // TODO: zdokumentovat
            ResponseBuilder builder = Response.status(Response.Status.FORBIDDEN);
            builder.type(MediaType.TEXT_PLAIN);
            builder.entity("Unknown format " + format);
            return builder.build();
        }
        }
    }

    private static Object buildEntityJson(String errorCode, String errorMessage) {
        try {
            JSONObject root = new JSONObject("error");
            root.put("code", errorCode);
            root.put("message", errorMessage);
            return root;
        } catch (JSONException e) {
            LOGGER.severe(e.getMessage());
            return "{}";
        }
    }

    private static String buildEntityXml(String errorCode, String errorMessage) {
        ErrorResponseBuilder builder = new ErrorResponseBuilder(errorCode, errorMessage);
        return builder.buildDocumentWithResponseHeader().toXML();
    }

    // public Status getStatus() {
    // return status;
    // }
    //
    // public String getErrorCode() {
    // return errorCode;
    // }
    //
    // public String getErrorMessage() {
    // return errorMessage;
    // }
}

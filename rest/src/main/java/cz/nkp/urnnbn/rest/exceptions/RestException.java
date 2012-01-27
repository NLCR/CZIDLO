/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest.exceptions;

import cz.nkp.urnnbn.xml.builders.ErrorResponseBuilder;
import java.util.Map;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Martin Řehánek
 */
public class RestException extends WebApplicationException {

    public RestException(Status status, String errorCode, String errorMessage) {
        super(buildResponse(status, errorCode, errorMessage, null));
        //    super(Response.status(status).entity(buildEntityXml(errorCode, errorMessage)).type("text/xml").build());
    }

    public RestException(Status status, String errorCode, String errorMessage, Map<String, Object> headers) {
        super(buildResponse(status, errorCode, errorMessage, headers));
    }

    private static Response buildResponse(Status status, String errorCode, String errorMessage, Map<String, Object> headers) {
        ResponseBuilder builder = Response.status(status);
        builder.entity(buildEntityXml(errorCode, errorMessage));
        builder.type("text/xml");
        if (headers != null) {
            for (String headerName : headers.keySet()) {
                builder.header(headerName, headers.get(headerName));
            }
        }
        return builder.build();
    }

    private static String buildEntityXml(String errorCode, String errorMessage) {
        ErrorResponseBuilder builder = new ErrorResponseBuilder(errorCode, errorMessage);
        return builder.buildDocument().toXML();
    }
}

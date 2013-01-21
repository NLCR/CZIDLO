/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.v3.exceptions;

import cz.nkp.urnnbn.xml.builders.ErrorResponseBuilder;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author Martin Řehánek
 */
public class ApiV3Exception extends WebApplicationException {

    private final Status status;
    private final String errorCode;
    private final String errorMessage;

    public ApiV3Exception(Status status, String errorCode, String errorMessage) {
        super(buildResponse(status, errorCode, errorMessage));
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    private static Response buildResponse(Status status, String errorCode, String errorMessage) {
        ResponseBuilder builder = Response.status(status);
        builder.entity(buildEntityXml(errorCode, errorMessage));
        builder.type("text/xml");
        return builder.build();
    }

    private static String buildEntityXml(String errorCode, String errorMessage) {
        ErrorResponseBuilder builder = new ErrorResponseBuilder(errorCode, errorMessage);
        return builder.buildDocumentWithResponseHeader().toXML();
    }

    public Status getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

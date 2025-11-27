package cz.nkp.urnnbn.czidlo_web_api.api.exceptions;

import cz.nkp.urnnbn.czidlo_web_api.api.ApiError;
import jakarta.json.stream.JsonParsingException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Context
    HttpHeaders headers;

    @Override
    public Response toResponse(Throwable exception) {

        if (exception instanceof UnknownRecordException) {
            return response(Response.Status.NOT_FOUND, exception.getMessage());
        }

        if (exception instanceof DuplicateRecordException) {
            return response(Response.Status.BAD_REQUEST, exception.getMessage());
        }

        if (exception instanceof BadArgumentException) {
            return response(Response.Status.BAD_REQUEST, exception.getMessage());
        }

        if (exception instanceof IllegalArgumentException) {
            return response(Response.Status.BAD_REQUEST, exception.getMessage());
        }

        if (exception instanceof BadRequestException) {
            return response(Response.Status.BAD_REQUEST, exception.getMessage());
        }

        if (exception instanceof AccessRightException) {
            return response(Response.Status.FORBIDDEN, exception.getMessage());
        }

        if (exception instanceof JsonParsingException) {
            return response(Response.Status.BAD_REQUEST, "Malformed JSON in request body: " + exception.getMessage());
        }

        return response(Response.Status.INTERNAL_SERVER_ERROR, "Internal server error: " + exception.getMessage());
    }

    private Response response(Response.Status status, String message) {
        return Response.status(status)
                .entity(new ApiError(message))
                .type(negotiateMediaType())
                .build();
    }

    /**
     * Negotiate response media type – only between JSON and XML
     */
    private MediaType negotiateMediaType() {

        java.util.List<MediaType> accepts = headers.getAcceptableMediaTypes();

        for (MediaType a : accepts) {
            if (a.isCompatible(MediaType.APPLICATION_JSON_TYPE)) return MediaType.APPLICATION_JSON_TYPE;
            if (a.isCompatible(MediaType.APPLICATION_XML_TYPE)) return MediaType.APPLICATION_XML_TYPE;
        }
        // fallback – never text/plain for errors
        return MediaType.APPLICATION_JSON_TYPE;
    }


}

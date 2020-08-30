package cz.nkp.urnnbn.api.v4.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

/**
 * The only exception here that does not extend ApiV4Exception. It always returns html no mether what the specified response format was. The error
 * was in incorrect format specification.
 *
 */
public class IllegalFormatException extends WebApplicationException {
    public IllegalFormatException(String format) {
        super(buildResponse(format));
    }

    private static Response buildResponse(String format) {
        String content = String.format("<!DOCTYPE html>" + //
                "<html lang=\"en\">" + //
                "<head>" + //
                "<meta charset=\"utf-8\"/>" + //
                "<title>CZIDLO</title>" + //
                "</head>" + //
                "<body>" + //
                "<h1>Unsupported format</h1>" + //
                "<p>Format <i>%s</i> is not allowed. Try with format=xml or format=json.</p>" + //
                "</body>" + //
                "</html>", format);

        ResponseBuilder builder = Response.status(Status.BAD_REQUEST);
        builder.entity(content.toString());
        builder.type(MediaType.TEXT_HTML);
        return builder.build();
    }
}

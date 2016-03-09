package cz.nkp.urnnbn.api.v4.exceptions;

import javax.ws.rs.core.Response.Status;

import cz.nkp.urnnbn.api.v4.ResponseFormat;

public class IllegalFormatException extends ApiV4Exception {
    public IllegalFormatException(ResponseFormat responseFormat, String requestedFormat) {
        super(responseFormat, Status.BAD_REQUEST, "ILLEGAL_FORMAT", String.format(
                "Format \"%s\" is not allowed. Try with format=xml or format=json.", requestedFormat));
    }
}

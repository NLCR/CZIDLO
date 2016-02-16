package cz.nkp.urnnbn.api.v4.exceptions;

import javax.ws.rs.core.Response.Status;

import cz.nkp.urnnbn.api.v3.exceptions.ApiV3Exception;

public class UnsupportedFormatException extends ApiV3Exception {

    public UnsupportedFormatException(String format) {
        super(Status.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_FORMAT", "Format " + format + " not supported");
    }

}

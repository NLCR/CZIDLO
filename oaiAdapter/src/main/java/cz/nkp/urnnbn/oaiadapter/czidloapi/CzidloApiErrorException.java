package cz.nkp.urnnbn.oaiadapter.czidloapi;

import cz.nkp.urnnbn.oaiadapter.czidloapi.CzidloApiError;

/**
 * Created by Martin Řehánek on 1.11.17.
 */
public class CzidloApiErrorException extends Exception {

    private final String url;
    private final int httpCode;
    private final CzidloApiError czidloApiError;

    public CzidloApiErrorException(String url, int httpCode, CzidloApiError czidloApiError) {
        super(buildMessage(url, httpCode, czidloApiError));
        this.url = url;
        this.httpCode = httpCode;
        this.czidloApiError = czidloApiError;
    }

    private static String buildMessage(String url, int httpCode, CzidloApiError czidloApiError) {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP ").append(httpCode);
        if (czidloApiError != null && czidloApiError.getErrorCode() != null) {
            builder.append(": ").append(czidloApiError.getErrorCode());
        }
        if (czidloApiError != null && czidloApiError.getErrorMessage() != null) {
            builder.append(": ").append(czidloApiError.getErrorMessage());
        }
        builder.append(";URL: ").append(url);
        return builder.toString();
    }

    public String getUrl() {
        return url;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public CzidloApiError getCzidloApiError() {
        return czidloApiError;
    }
}

package cz.nkp.urnnbn.api_client.v5;

/**
 * Created by Martin Řehánek on 1.11.17.
 */
public class CzidloApiError {

    private final String errorCode;
    private final String errorMessage;

    public CzidloApiError(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

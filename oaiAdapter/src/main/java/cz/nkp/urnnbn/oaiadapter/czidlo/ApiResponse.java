package cz.nkp.urnnbn.oaiadapter.czidlo;

/**
 * Created by Martin Řehánek on 1.11.17.
 */
public class ApiResponse {

    private final int httpCode;
    private final String body;

    public ApiResponse(int httpCode, String body) {
        this.httpCode = httpCode;
        this.body = body;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "httpCode=" + httpCode +
                ", body='" + body + '\'' +
                '}';
    }
}

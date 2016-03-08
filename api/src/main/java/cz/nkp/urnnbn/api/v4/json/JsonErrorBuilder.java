package cz.nkp.urnnbn.api.v4.json;

import java.util.logging.Logger;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class JsonErrorBuilder implements JsonBuilder {

    private static final Logger LOGGER = Logger.getLogger(JsonErrorBuilder.class.getName());
    private final String errorCode;
    private final String errorMessage;

    public JsonErrorBuilder(String errorCode, String errorMessage) {
        if (errorCode == null) {
            throw new NullPointerException("errorCode");
        }
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public String toJson() {
        try {
            JSONObject root = new JSONObject();
            JSONObject error = new JSONObject();
            root.put("error", error);
            error.put("code", errorCode);
            error.put("message", errorMessage);
            return root.toString();
        } catch (JSONException e) {
            LOGGER.severe(e.getMessage());
            return EMPTY_JSON;
        }
    }

}

package cz.nkp.urnnbn.api.v5.json;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class JsonErrorBuilder extends JsonBuilder {

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
    public String getName() {
        return "error";
    }

    @Override
    public JSONObject build() {
        try {
            JSONObject root = new JSONObject();
            root.put("code", errorCode);
            root.put("message", errorMessage);
            return root;
        } catch (JSONException e) {
            LOGGER.severe(e.getMessage());
            return EMPTY_OBJECT;
        }
    }

}

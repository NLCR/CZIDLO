package cz.nkp.urnnbn.api.v4.json;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class DigitalDocumentsBuilderJson extends JsonBuilder {

    private final int digDocsCount;

    public DigitalDocumentsBuilderJson(int digDocsCount) {
        this.digDocsCount = digDocsCount;
    }

    @Override
    protected String getName() {
        return "digitalDocuments";
    }

    @Override
    protected JSONObject build() {
        try {
            JSONObject root = new JSONObject();
            root.put("count", digDocsCount);
            return root;
        } catch (JSONException e) {
            LOGGER.severe(e.getMessage());
            return EMPTY_OBJECT;
        }

    }

}

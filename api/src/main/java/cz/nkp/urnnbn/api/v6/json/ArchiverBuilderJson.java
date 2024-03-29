package cz.nkp.urnnbn.api.v6.json;

import cz.nkp.urnnbn.core.dto.Archiver;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ArchiverBuilderJson extends JsonBuilder {

    private final Archiver archiver;

    public ArchiverBuilderJson(Archiver archiver) {
        this.archiver = archiver;
    }

    @Override
    protected String getName() {
        return "archiver";
    }

    @Override
    protected JSONObject build() {
        try {
            JSONObject root = new JSONObject();
            root.put("id", archiver.getId());
            appendElementWithContentIfNotNull(root, archiver.getName(), "name");
            appendElementWithContentIfNotNull(root, archiver.getDescription(), "description");
            appendTimestamps(root, archiver);
            return root;
        } catch (JSONException e) {
            LOGGER.severe(e.getMessage());
            return EMPTY_OBJECT;
        }
    }
}

package cz.nkp.urnnbn.api.v6.json;

import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class DigitalLibraryBuilderJson extends JsonBuilder {

    private final DigitalLibrary lib;
    private final RegistrarBuilderJson registrarBuilder;

    public DigitalLibraryBuilderJson(DigitalLibrary lib, RegistrarBuilderJson registrarBuilder) {
        this.lib = lib;
        this.registrarBuilder = registrarBuilder;
    }

    @Override
    public String getName() {
        return "digitalLibrary";
    }

    @Override
    public JSONObject build() {
        try {
            JSONObject root = new JSONObject();
            root.put("id", lib.getId());
            appendElementWithContentIfNotNull(root, lib.getName(), "name");
            appendElementWithContentIfNotNull(root, lib.getDescription(), "description");
            appendElementWithContentIfNotNull(root, lib.getUrl(), "url");
            appendTimestamps(root, lib);
            appendBuilderResultfNotNull(root, registrarBuilder);
            return root;
        } catch (JSONException e) {
            LOGGER.severe(e.getMessage());
            return EMPTY_OBJECT;
        }
    }

}

package cz.nkp.urnnbn.api.v4.json;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import cz.nkp.urnnbn.core.dto.Catalog;

public class CatalogBuilderJson extends JsonBuilder {

    private final Catalog catalog;

    public CatalogBuilderJson(Catalog catalog) {
        this.catalog = catalog;
    }

    @Override
    protected String getName() {
        return "catalog";
    }

    @Override
    protected JSONObject build() {
        try {
            JSONObject root = new JSONObject();
            root.put("id", catalog.getId());
            appendElementWithContentIfNotNull(root, catalog.getName(), "name");
            appendElementWithContentIfNotNull(root, catalog.getDescription(), "description");
            appendElementWithContentIfNotNull(root, catalog.getUrlPrefix(), "urlPrefix");
            appendTimestamps(root, catalog);
            return root;
        } catch (JSONException e) {
            LOGGER.severe(e.getMessage());
            return EMPTY_OBJECT;
        }

    }
}

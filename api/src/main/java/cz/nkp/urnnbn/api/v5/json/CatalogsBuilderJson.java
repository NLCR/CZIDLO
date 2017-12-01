package cz.nkp.urnnbn.api.v5.json;

import cz.nkp.urnnbn.core.dto.Catalog;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import java.util.List;

public class CatalogsBuilderJson extends JsonBuilder {

    private final List<Catalog> catalogs;

    public CatalogsBuilderJson(List<Catalog> catalogs) {
        this.catalogs = catalogs;
    }

    @Override
    protected String getName() {
        return "catalogs";
    }

    @Override
    protected JSONArray build() {
        JSONArray array = new JSONArray();
        try {
            for (Catalog catalog : catalogs) {
                CatalogBuilderJson builder = new CatalogBuilderJson(catalog);
                appendBuilderResultfNotNull(array, builder);
            }
            return array;
        } catch (JSONException e) {
            LOGGER.severe(e.getMessage());
            return EMPTY_ARRAY;
        }
    }

}

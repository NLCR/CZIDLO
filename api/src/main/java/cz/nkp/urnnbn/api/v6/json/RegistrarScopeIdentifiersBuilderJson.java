package cz.nkp.urnnbn.api.v6.json;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import java.util.List;

public class RegistrarScopeIdentifiersBuilderJson extends JsonBuilder {

    private final List<RegistrarScopeIdentifierBuilderJson> rsidBuilders;

    public RegistrarScopeIdentifiersBuilderJson(List<RegistrarScopeIdentifierBuilderJson> rsidBuilders) {
        this.rsidBuilders = rsidBuilders;
    }

    @Override
    protected String getName() {
        return "registrarScopeIdentifiers";
    }

    @Override
    public JSONArray build() {
        try {
            if (rsidBuilders != null) {
                JSONArray array = new JSONArray();
                for (RegistrarScopeIdentifierBuilderJson rsidBuilder : rsidBuilders) {
                    appendBuilderResultfNotNull(array, rsidBuilder);
                }
                return array;
            } else {
                return EMPTY_ARRAY;
            }
        } catch (JSONException e) {
            LOGGER.severe(e.getMessage());
            return EMPTY_ARRAY;
        }
    }

}

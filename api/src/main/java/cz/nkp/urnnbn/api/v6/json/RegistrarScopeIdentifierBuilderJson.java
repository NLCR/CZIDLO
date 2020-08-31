package cz.nkp.urnnbn.api.v6.json;

import cz.nkp.urnnbn.core.RegistrarScopeIdValue;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class RegistrarScopeIdentifierBuilderJson extends JsonBuilder {

    private final RegistrarScopeIdentifier id;
    private final RegistrarScopeIdValue previousValue;

    public RegistrarScopeIdentifierBuilderJson(RegistrarScopeIdentifier id) {
        this.id = id;
        this.previousValue = null;
    }

    public RegistrarScopeIdentifierBuilderJson(RegistrarScopeIdentifier id, RegistrarScopeIdValue previousValue) {
        this.id = id;
        this.previousValue = previousValue;
    }

    @Override
    protected String getName() {
        return "id";
    }

    @Override
    public JSONObject build() {
        try {
            JSONObject root = new JSONObject();
            root.put("type", id.getType().toString());
            root.put("value", id.getValue().toString());
            if (previousValue != null) {
                root.put("previousValue", previousValue.toString());
            }
            return root;
        } catch (JSONException e) {
            LOGGER.severe(e.getMessage());
            return EMPTY_OBJECT;
        }
    }

}

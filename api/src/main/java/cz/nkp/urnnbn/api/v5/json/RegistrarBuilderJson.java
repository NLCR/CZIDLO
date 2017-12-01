package cz.nkp.urnnbn.api.v5.json;

import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.core.dto.Registrar;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class RegistrarBuilderJson extends JsonBuilder {

    private final Registrar registrar;
    private final DigitalLibrariesBuilderJson librariesBuilder;
    private final CatalogsBuilderJson catalogsBuilder;

    public RegistrarBuilderJson(Registrar registrar, DigitalLibrariesBuilderJson libsBuilder, CatalogsBuilderJson catsBuilder) {
        this.registrar = registrar;
        this.librariesBuilder = libsBuilder;
        this.catalogsBuilder = catsBuilder;
    }

    @Override
    public String getName() {
        return "registrar";
    }

    @Override
    public JSONObject build() {
        try {
            JSONObject root = new JSONObject();
            root.put("id", registrar.getId());
            root.put("code", registrar.getCode().toString());
            appendElementWithContentIfNotNull(root, registrar.getName(), "name");
            appendElementWithContentIfNotNull(root, registrar.getDescription(), "description");
            appendTimestamps(root, registrar);
            appendRegistrationModes(root);
            appendBuilderResultfNotNull(root, librariesBuilder);
            appendBuilderResultfNotNull(root, catalogsBuilder);
            return root;
        } catch (JSONException e) {
            LOGGER.severe(e.getMessage());
            return EMPTY_OBJECT;
        }
    }

    private void appendRegistrationModes(JSONObject registrarEl) throws JSONException {
        JSONObject modesEl = new JSONObject();
        registrarEl.put("registrationModes", modesEl);
        for (UrnNbnRegistrationMode mode : UrnNbnRegistrationMode.values()) {
            Boolean enabled = registrar.isRegistrationModeAllowed(mode);
            modesEl.put(mode.name(), enabled);
        }
    }

}

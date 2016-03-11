package cz.nkp.urnnbn.api.v4.json;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import cz.nkp.urnnbn.core.dto.DigitalInstance;

public class DigitalInstanceBuilderJson extends JsonBuilder {

    private final DigitalInstance instance;
    private final DigitalLibraryBuilderJson digLibBuilder;
    private final Long digLibId;
    private final DigitalDocumentBuilderJson digDocBuilder;

    public DigitalInstanceBuilderJson(DigitalInstance instance, DigitalLibraryBuilderJson digLibBuilder, DigitalDocumentBuilderJson digDocBuilder) {
        this.instance = instance;
        this.digLibBuilder = digLibBuilder;
        this.digLibId = null;
        this.digDocBuilder = digDocBuilder;
    }

    public DigitalInstanceBuilderJson(DigitalInstance instance, Long digLibId) {
        this.instance = instance;
        this.digLibBuilder = null;
        this.digLibId = digLibId;
        this.digDocBuilder = null;
    }

    @Override
    public String getName() {
        return "digitalInstance";
    }

    @Override
    public JSONObject build() {
        try {
            JSONObject root = new JSONObject();
            root.put("id", instance.getId());
            root.put("active", instance.isActive());
            appendElementWithContentIfNotNull(root, instance.getUrl(), "url");
            appendElementWithContentIfNotNull(root, instance.getFormat(), "format");
            appendElementWithContentIfNotNull(root, instance.getAccessibility(), "accessibility");
            appendElementWithContentIfNotNull(root, digLibId, "digitalLibraryId");
            appendElementWithContentIfNotNull(root, instance.getCreated(), "created");
            appendElementWithContentIfNotNull(root, instance.getDeactivated(), "deactivated");
            appendBuilderResultfNotNull(root, digLibBuilder);
            appendBuilderResultfNotNull(root, digDocBuilder);
            return root;
        } catch (JSONException e) {
            LOGGER.severe(e.getMessage());
            return EMPTY_OBJECT;
        }
    }

}

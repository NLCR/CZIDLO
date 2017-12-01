package cz.nkp.urnnbn.api.v5.json;

import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class UrnNbnBuilderJson extends JsonBuilder {

    private final UrnNbnWithStatus urnNbnWithStatus;

    public UrnNbnBuilderJson(UrnNbnWithStatus urnNbnWithStatus) {
        this.urnNbnWithStatus = urnNbnWithStatus;
    }

    @Override
    protected String getName() {
        return "urnNbn";
    }

    @Override
    protected JSONObject build() {
        try {
            JSONObject root = new JSONObject();
            root.put("value", urnNbnWithStatus.getUrn().toString());
            root.put("status", urnNbnWithStatus.getStatus().name());
            appendElementWithContentIfNotNull(root, urnNbnWithStatus.getUrn().getDeactivationNote(), "deactivationNote");
            root.put("registrarCode", urnNbnWithStatus.getUrn().getRegistrarCode().toString());
            root.put("documentCode", urnNbnWithStatus.getUrn().getDocumentCode());
            root.put("digitalDocumentId", urnNbnWithStatus.getUrn().getDigDocId());
            appendElementWithContentIfNotNull(root, urnNbnWithStatus.getUrn().getReserved(), "reserved");
            appendElementWithContentIfNotNull(root, urnNbnWithStatus.getUrn().getRegistered(), "registered");
            appendElementWithContentIfNotNull(root, urnNbnWithStatus.getUrn().getDeactivated(), "deactivated");
            appendPredecessors(root, urnNbnWithStatus.getUrn());
            appendSuccessors(root, urnNbnWithStatus.getUrn());
            return root;
        } catch (JSONException e) {
            LOGGER.severe(e.getMessage());
            return EMPTY_OBJECT;
        }
    }

}

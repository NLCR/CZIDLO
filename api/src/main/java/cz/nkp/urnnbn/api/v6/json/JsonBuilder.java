package cz.nkp.urnnbn.api.v6.json;

import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.IdentifiableWithDatestamps;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;

import java.util.List;
import java.util.logging.Logger;

public abstract class JsonBuilder {

    public static final Logger LOGGER = Logger.getLogger(JsonBuilder.class.getName());

    public static final JSONObject EMPTY_OBJECT = new JSONObject();
    public static final JSONArray EMPTY_ARRAY = new JSONArray();

    protected abstract String getName();

    protected abstract Object build();

    public String toJson() {
        try {
            JSONObject root = new JSONObject();
            root.put(getName(), build());
            return root.toString();
        } catch (JSONException e) {
            LOGGER.severe(e.getMessage());
            return "{}";
        }
    }

    protected void appendBuilderResultfNotNull(JSONArray array, JsonBuilder builder) throws JSONException {
        if (builder != null) {
            Object built = builder.build();
            if (built != null) {
                array.put(built);
            }
        }
    }

    protected void appendBuilderResultfNotNull(JSONObject root, JsonBuilder builder) throws JSONException {
        if (builder != null) {
            Object built = builder.build();
            if (built != null) {
                root.put(builder.getName(), built);
            }
        }
    }

    protected void appendElementWithContentIfNotNull(JSONObject object, DateTime value, String key) throws JSONException {
        if (value != null) {
            object.put(key, value.toString());
        }
    }

    protected void appendElementWithContentIfNotNull(JSONObject object, Boolean value, String key) throws JSONException {
        if (value != null) {
            object.put(key, value);
        }
    }

    protected void appendElementWithContentIfNotNull(JSONObject object, Integer value, String key) throws JSONException {
        if (value != null) {
            object.put(key, value);
        }
    }

    protected void appendElementWithContentIfNotNull(JSONObject object, Long value, String key) throws JSONException {
        if (value != null) {
            object.put(key, value);
        }
    }

    protected void appendElementWithContentIfNotNull(JSONObject object, String value, String key) throws JSONException {
        if (value != null && !value.isEmpty()) {
            object.put(key, value);
        }
    }

    protected final void appendTimestamps(JSONObject rootElement, IdentifiableWithDatestamps entity) throws JSONException {
        DateTime created = entity.getCreated();
        if (created != null) {
            appendElementWithContentIfNotNull(rootElement, entity.getCreated(), "created");
        }
        DateTime modified = entity.getModified();
        if (modified != null && !modified.equals(created)) {
            appendElementWithContentIfNotNull(rootElement, entity.getModified(), "modified");
        }
    }

    protected JSONObject appendElement(JSONObject parent, String key) throws JSONException {
        JSONObject child = new JSONObject();
        parent.put(key, child);
        return child;
    }

    protected void appendPredecessors(JSONObject root, UrnNbn urn) throws JSONException {
        List<UrnNbnWithStatus> predecessors = urn.getPredecessors();
        if (predecessors != null && !predecessors.isEmpty()) {
            JSONArray array = new JSONArray();
            for (UrnNbnWithStatus predecessor : predecessors) {
                JSONObject predecessorEl = new JSONObject();
                predecessorEl.put("urnNbn", predecessor.getUrn().toString());
                if (predecessor.getNote() != null) {
                    predecessorEl.put("note", predecessor.getNote());
                }
                array.put(predecessorEl);

            }
            root.put("predecessors", array);
        }
    }

    protected void appendSuccessors(JSONObject root, UrnNbn urn) throws JSONException {
        List<UrnNbnWithStatus> successors = urn.getSuccessors();
        if (successors != null && !successors.isEmpty()) {
            JSONArray array = new JSONArray();
            for (UrnNbnWithStatus successor : successors) {
                JSONObject sucessorEl = new JSONObject();
                sucessorEl.put("urnNbn", successor.getUrn().toString());
                if (successor.getNote() != null) {
                    sucessorEl.put("note", successor.getNote());
                }
                array.put(sucessorEl);
            }
            root.put("successors", array);
        }
    }

}

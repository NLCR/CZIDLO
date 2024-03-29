package cz.nkp.urnnbn.api.v6.json;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.List;

public class DigitalInstancesBuilderJson extends JsonBuilder {

    private final long count;
    private final List<DigitalInstanceBuilderJson> instanceBuilders;

    public DigitalInstancesBuilderJson(long count) {
        this.count = count;
        this.instanceBuilders = null;
    }

    public DigitalInstancesBuilderJson(List<DigitalInstanceBuilderJson> instancesBuilders) {
        this.count = instancesBuilders.size();
        this.instanceBuilders = instancesBuilders;
    }

    @Override
    public String getName() {
        return "digitalInstances";
    }

    @Override
    public Object build() {
        try {
            if (instanceBuilders != null) {
                JSONArray array = new JSONArray();
                for (DigitalInstanceBuilderJson builder : instanceBuilders) {
                    JSONObject built = builder.build();
                    array.put(built);
                }
                return array;
            } else {
                JSONObject root = new JSONObject();
                root.put("count", count);
                return root;
            }
        } catch (JSONException e) {
            LOGGER.severe(e.getMessage());
            return EMPTY_OBJECT;
        }
    }
}

package cz.nkp.urnnbn.api.v6.json;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DigitalDocumentsBuilderJson extends JsonBuilder {

    private final Integer digDocsCount;
    private final List<DigitalDocumentBuilderJson> digDocBuilders;

    private DigitalDocumentsBuilderJson(Integer digDocsCount, List<DigitalDocumentBuilderJson> digDocBuilders) {
        this.digDocsCount = digDocsCount;
        this.digDocBuilders = digDocBuilders;
    }

    public DigitalDocumentsBuilderJson(List<DigitalDocumentBuilderJson> digDocBuilders) {
        this(null, digDocBuilders);
    }

    public DigitalDocumentsBuilderJson(Integer digDocsCount) {
        this(digDocsCount, null);
    }

    @Override
    protected String getName() {
        return "digitalDocuments";
    }

    @Override
    protected JSONObject build() {
        try {
            JSONObject root = new JSONObject();
            if (digDocsCount != null) {
                root.put("count", digDocsCount);
            } else if (digDocBuilders != null) {
                root.put("count", digDocBuilders.size());
                List<JSONObject> docs = new ArrayList<>();
                for (DigitalDocumentBuilderJson digDocBuilder : digDocBuilders) {
                    docs.add(digDocBuilder.build());
                }
                root.put("items", docs);
            }
            return root;
        } catch (JSONException e) {
            LOGGER.severe(e.getMessage());
            return EMPTY_OBJECT;
        }

    }

}

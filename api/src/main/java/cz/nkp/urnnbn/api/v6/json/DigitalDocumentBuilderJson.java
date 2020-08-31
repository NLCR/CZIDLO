package cz.nkp.urnnbn.api.v6.json;

import cz.nkp.urnnbn.api.v6.json.ie.IntelectualEntityBuilderJson;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class DigitalDocumentBuilderJson extends JsonBuilder {

    private final DigitalDocument doc;
    private final UrnNbn urn;
    private final RegistrarScopeIdentifiersBuilderJson rsIdsBuilder;
    private final DigitalInstancesBuilderJson instancesBuilder;
    private final RegistrarBuilderJson registrarBuilder;
    private final ArchiverBuilderJson archiverBuilder;
    private final IntelectualEntityBuilderJson entityBuilder;

    public DigitalDocumentBuilderJson(DigitalDocument doc, UrnNbn urn, RegistrarScopeIdentifiersBuilderJson rsIdsBuilder,
                                      DigitalInstancesBuilderJson instancesBuilder, RegistrarBuilderJson registrarBuilder, ArchiverBuilderJson archiverBuilder,
                                      IntelectualEntityBuilderJson entityBuilder) {
        this.doc = doc;
        this.urn = urn;
        this.rsIdsBuilder = rsIdsBuilder;
        this.instancesBuilder = instancesBuilder;
        this.registrarBuilder = registrarBuilder;
        this.archiverBuilder = archiverBuilder;
        this.entityBuilder = entityBuilder;
    }

    @Override
    public String getName() {
        return "digitalDocument";
    }

    @Override
    public JSONObject build() {
        try {
            JSONObject root = new JSONObject();
            root.put("id", doc.getId());
            root.put("urnNbn", urn.toString());
            appendPredecessors(root, urn);
            appendSuccessors(root, urn);
            appendElementWithContentIfNotNull(root, doc.getFinancedFrom(), "financed");
            appendElementWithContentIfNotNull(root, doc.getContractNumber(), "contractNumber");
            appendTimestamps(root, doc);
            appendBuilderResultfNotNull(root, entityBuilder);
            appendTechnicalMetadata(root);
            appendBuilderResultfNotNull(root, rsIdsBuilder);
            appendBuilderResultfNotNull(root, registrarBuilder);
            appendBuilderResultfNotNull(root, archiverBuilder);
            appendBuilderResultfNotNull(root, instancesBuilder);
            return root;
        } catch (JSONException e) {
            LOGGER.severe(e.getMessage());
            return EMPTY_OBJECT;
        }
    }

    private void appendTechnicalMetadata(JSONObject root) throws JSONException {
        JSONObject technicalEl = appendElement(root, "technicalMetadata");
        // format
        String format = doc.getFormat();
        String formatVersion = doc.getFormatVersion();
        if (format != null || formatVersion != null) {
            JSONObject formatEl = appendElement(technicalEl, "format");
            if (format != null) {
                formatEl.put("format", format);
            }
            if (formatVersion != null) {
                formatEl.put("version", formatVersion);
            }
        }
        // extent
        appendElementWithContentIfNotNull(technicalEl, doc.getExtent(), "extent");
        // resolution
        Integer resolutionHorizontal = doc.getResolutionHorizontal();
        Integer resolutionVertical = doc.getResolutionVertical();
        if (resolutionHorizontal != null || resolutionVertical != null) {
            JSONObject resolutionEl = appendElement(technicalEl, "resolution");
            appendElementWithContentIfNotNull(resolutionEl, resolutionHorizontal, "horizontal");
            appendElementWithContentIfNotNull(resolutionEl, resolutionVertical, "vertical");
        }
        // compression
        String compression = doc.getCompression();
        Double compressionRatio = doc.getCompressionRatio();
        if (compression != null || compressionRatio != null) {
            JSONObject compressionEl = appendElement(technicalEl, "compression");
            if (compression != null) {
                compressionEl.put("standard", compression);
            }
            if (compressionRatio != null) {
                compressionEl.put("ratio", compressionRatio);
            }
        }
        // color
        String colorModel = doc.getColorModel();
        Integer colorDepth = doc.getColorDepth();
        if (colorModel != null || colorDepth != null) {
            JSONObject colorEl = appendElement(technicalEl, "color");
            appendElementWithContentIfNotNull(colorEl, colorModel, "model");
            appendElementWithContentIfNotNull(colorEl, colorDepth, "depth");
        }
        // iccProfile
        appendElementWithContentIfNotNull(technicalEl, doc.getIccProfile(), "iccProfile");
        // picture size
        Integer pictureWidth = doc.getPictureWidth();
        Integer pictureHeight = doc.getPictureHeight();
        if (pictureWidth != null || pictureHeight != null) {
            JSONObject pictureEl = appendElement(technicalEl, "pictureSize");
            appendElementWithContentIfNotNull(pictureEl, pictureWidth, "width");
            appendElementWithContentIfNotNull(pictureEl, pictureHeight, "height");
        }
    }

}

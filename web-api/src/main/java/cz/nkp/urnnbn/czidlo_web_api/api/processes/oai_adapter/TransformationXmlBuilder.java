package cz.nkp.urnnbn.czidlo_web_api.api.processes.oai_adapter;

import cz.nkp.urnnbn.processmanager.core.XmlTransformation;

public class TransformationXmlBuilder {

    private final XmlTransformation transformation;

    public TransformationXmlBuilder(XmlTransformation transformation) {
        this.transformation = transformation;
    }

    public String buildXml() {
        StringBuilder result = new StringBuilder();
        result.append("<transformation>");
        appendElementIfContentNotNull(result, "id", transformation.getId());
        appendElementIfContentNotNull(result, "type", transformation.getType());
        appendElementIfContentNotNull(result, "name", transformation.getName());
        appendElementIfContentNotNull(result, "description", transformation.getDescription());
        appendElementIfContentNotNull(result, "created", transformation.getCreated());
        result.append("</transformation>");
        return result.toString();
    }

    private void appendElementIfContentNotNull(StringBuilder result, String elementName, Object content) {
        if (content != null) {
            result.append('<').append(elementName).append('>');
            result.append(content.toString());
            result.append("</").append(elementName).append('>');
        }
    }
}

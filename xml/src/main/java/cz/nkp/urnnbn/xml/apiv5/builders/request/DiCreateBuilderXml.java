package cz.nkp.urnnbn.xml.apiv5.builders.request;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.xml.apiv5.builders.XmlBuilder;
import nu.xom.Element;

/**
 * Created by Martin Řehánek on 27.10.17.
 */
public class DiCreateBuilderXml extends XmlBuilder {

    private final DigitalInstance instance;

    public DiCreateBuilderXml(DigitalInstance instance) {
        this.instance = instance;
    }

    @Override
    public Element buildRootElement() {
        Element root = new Element("digitalInstance", CZIDLO_NS);
        appendElementWithContentIfNotNull(root, instance.getUrl(), "url");
        appendElementWithContentIfNotNull(root, instance.getLibraryId(), "digitalLibraryId");
        appendElementWithContentIfNotNull(root, instance.getFormat(), "format");
        appendElementWithContentIfNotNull(root, instance.getAccessibility(), "accessibility");
        return root;
    }
}

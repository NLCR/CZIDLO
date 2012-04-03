/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import java.util.List;
import nu.xom.Attribute;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalDocumentBuilder extends XmlBuilder {

    private final DigitalDocument doc;
    private final UrnNbn urn;
    private final RegistrarScopeIdentifiersBuilder identifiersBuilder;
    private final List<DigitalInstanceBuilder> instanceBuilderList;
    private final RegistrarBuilder registrarBuilder;
    private final ArchiverBuilder archiverBuilder;
    private final IntelectualEntityBuilder entityBuilder;

    public DigitalDocumentBuilder(DigitalDocument rep, UrnNbn urn, RegistrarScopeIdentifiersBuilder identifiersBuilder, List<DigitalInstanceBuilder> instanceBuilders, RegistrarBuilder registrarBuilder, ArchiverBuilder archiverBuilder, IntelectualEntityBuilder entityBuilder) {
        this.doc = rep;
        this.urn = urn;
        this.identifiersBuilder = identifiersBuilder;
        this.instanceBuilderList = instanceBuilders;
        this.registrarBuilder = registrarBuilder;
        this.archiverBuilder = archiverBuilder;
        this.entityBuilder = entityBuilder;
    }

    @Override
    public Element buildRootElement() {
        Element root = new Element("digitalDocument", RESOLVER);
        //appendIdentifierElement(root, "INTERNAL", rep.getId());
        appendTimestamps(root, doc, "digital document");
        appendElementWithContentIfNotNull(root, urn, "urnNbn");
        if (identifiersBuilder != null) {
            appendBuilderResultfNotNull(root, identifiersBuilder);
        }
        appendElementWithContentIfNotNull(root, doc.getFinancedFrom(), "financed");
        appendElementWithContentIfNotNull(root, doc.getContractNumber(), "contractNumber");
        appendTechnicalMetadata(root);
        if (instanceBuilderList != null) {
            for (DigitalInstanceBuilder builder : instanceBuilderList) {
                appendBuilderResultfNotNull(root, builder);
            }
        }
        appendBuilderResultfNotNull(root, registrarBuilder);
        appendBuilderResultfNotNull(root, archiverBuilder);
        appendBuilderResultfNotNull(root, entityBuilder);
        return root;
    }

    private void appendTechnicalMetadata(Element root) {
        Element technicalEl = appendElement(root, "technicalMetadata");

        //format
        Element formatEl = appendElement(technicalEl, "format");
        String format = doc.getFormat();
        if (format != null) {
            formatEl.appendChild(format);
        }
        String formatVersion = doc.getFormatVersion();
        if (formatVersion != null) {
            Attribute version = new Attribute("version", formatVersion);
            formatEl.addAttribute(version);
        }
        //extent
        appendElementWithContentIfNotNull(technicalEl, doc.getExtent(), "extent");
        //resolution
        Element resolutionEl = appendElement(technicalEl, "resolution");
        appendElementWithContentIfNotNull(resolutionEl, doc.getResolutionHorizontal(), "horizontal");
        appendElementWithContentIfNotNull(resolutionEl, doc.getResolutionVertical(), "vertical");
        //compression
        Element compressionEl = appendElement(technicalEl, "compression");
        String compression = doc.getCompression();
        if (compression != null) {
            compressionEl.appendChild(compression);
        }
        Double compressionRatio = doc.getCompressionRatio();
        if (compressionRatio != null) {
            Attribute ratio = new Attribute("ratio", compressionRatio.toString());
            compressionEl.addAttribute(ratio);
        }
        //color
        Element colorEl = appendElement(technicalEl, "color");
        appendElementWithContentIfNotNull(colorEl, doc.getColorModel(), "model");
        appendElementWithContentIfNotNull(colorEl, doc.getColorDepth(), "depth");
        appendElementWithContentIfNotNull(technicalEl, doc.getIccProfile(), "iccProfile");
        //common picture characteristics
        Element pictureEl = appendElement(technicalEl, "pictureSize");
        appendElementWithContentIfNotNull(pictureEl, doc.getPictureWidth(), "width");
        appendElementWithContentIfNotNull(pictureEl, doc.getPictureHeight(), "height");
    }
}

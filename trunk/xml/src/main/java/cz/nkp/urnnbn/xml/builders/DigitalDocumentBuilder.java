/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
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
    private final DigitalInstancesBuilder instancesBuilder;
    private final RegistrarBuilder registrarBuilder;
    private final ArchiverBuilder archiverBuilder;
    private final IntelectualEntityBuilder entityBuilder;

    public DigitalDocumentBuilder(DigitalDocument doc, UrnNbn urn, RegistrarScopeIdentifiersBuilder identifiersBuilder, DigitalInstancesBuilder instancesBuilder, RegistrarBuilder registrarBuilder, ArchiverBuilder archiverBuilder, IntelectualEntityBuilder entityBuilder) {
        this.doc = doc;
        this.urn = urn;
        this.identifiersBuilder = identifiersBuilder;
        this.instancesBuilder = instancesBuilder;
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
        appendElementWithContentIfNotNull(root, doc.getFinancedFrom(), "financed");
        appendElementWithContentIfNotNull(root, doc.getContractNumber(), "contractNumber");
        appendBuilderResultfNotNull(root, entityBuilder);
        appendTechnicalMetadata(root);
        appendBuilderResultfNotNull(root, registrarBuilder);
        appendBuilderResultfNotNull(root, identifiersBuilder);
        appendBuilderResultfNotNull(root, archiverBuilder);
        appendBuilderResultfNotNull(root, instancesBuilder);
        return root;
    }

    private void appendTechnicalMetadata(Element root) {
        Element technicalEl = appendElement(root, "technicalMetadata");
        //format
        String format = doc.getFormat();
        String formatVersion = doc.getFormatVersion();
        if (format != null || formatVersion != null) {
            Element formatEl = appendElement(technicalEl, "format");
            if (format != null) {
                formatEl.appendChild(format);
            }
            if (formatVersion != null) {
                formatEl.addAttribute(new Attribute("version", formatVersion));
            }
        }
        //extent
        appendElementWithContentIfNotNull(technicalEl, doc.getExtent(), "extent");
        //resolution
        Integer resolutionHorizontal = doc.getResolutionHorizontal();
        Integer resolutionVertical = doc.getResolutionVertical();
        if (resolutionHorizontal != null || resolutionVertical != null) {
            Element resolutionEl = appendElement(technicalEl, "resolution");
            appendElementWithContentIfNotNull(resolutionEl, resolutionHorizontal, "horizontal");
            appendElementWithContentIfNotNull(resolutionEl, resolutionVertical, "vertical");
        }
        //compression
        String compression = doc.getCompression();
        Double compressionRatio = doc.getCompressionRatio();
        if (compression != null || compressionRatio != null) {
            Element compressionEl = appendElement(technicalEl, "compression");
            if (compression != null) {
                compressionEl.appendChild(compression);
            }
            if (compressionRatio != null) {
                Attribute ratio = new Attribute("ratio", compressionRatio.toString());
                compressionEl.addAttribute(ratio);
            }
        }
        //color
        String colorModel = doc.getColorModel();
        Integer colorDepth = doc.getColorDepth();
        if (colorModel != null || colorDepth != null) {
            Element colorEl = appendElement(technicalEl, "color");
            appendElementWithContentIfNotNull(colorEl, colorModel, "model");
            appendElementWithContentIfNotNull(colorEl, colorDepth, "depth");
        }
        //iccProfile
        appendElementWithContentIfNotNull(technicalEl, doc.getIccProfile(), "iccProfile");
        //picture size
        Integer pictureWidth = doc.getPictureWidth();
        Integer pictureHeight = doc.getPictureHeight();
        if (pictureWidth != null || pictureHeight != null) {
            Element pictureEl = appendElement(technicalEl, "pictureSize");
            appendElementWithContentIfNotNull(pictureEl, pictureWidth, "width");
            appendElementWithContentIfNotNull(pictureEl, pictureHeight, "height");
        }
    }
}

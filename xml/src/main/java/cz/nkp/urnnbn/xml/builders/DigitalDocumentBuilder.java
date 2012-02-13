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
    private final DigitalDocumentIdentifiersBuilder identifiersBuilder;
    private final List<DigitalInstanceBuilder> instanceBuilderList;
    private final RegistrarBuilder registrarBuilder;
    private final ArchiverBuilder archiverBuilder;
    private final IntelectualEntityBuilder entityBuilder;

    public DigitalDocumentBuilder(DigitalDocument rep, UrnNbn urn, DigitalDocumentIdentifiersBuilder identifiersBuilder, List<DigitalInstanceBuilder> instanceBuilders, RegistrarBuilder registrarBuilder, ArchiverBuilder archiverBuilder, IntelectualEntityBuilder entityBuilder) {
        this.doc = rep;
        this.urn = urn;
        this.identifiersBuilder = identifiersBuilder;
        this.instanceBuilderList = instanceBuilders;
        this.registrarBuilder = registrarBuilder;
        this.archiverBuilder = archiverBuilder;
        this.entityBuilder = entityBuilder;
    }

    public Element buildRootElement() {
        Element root = new Element("digitalDocument", RESOLVER);
        //appendIdentifierElement(root, "INTERNAL", rep.getId());
        appendIdentifierElement(root, "URN:NBN", urn);
        if (identifiersBuilder != null) {
            appendBuilderResultfNotNull(root, identifiersBuilder);
        }
        appendElementWithContentIfNotNull(root, doc.getCreated(), "created");
        appendElementWithContentIfNotNull(root, doc.getLastUpdated(), "lastUpdated");
        appendElementWithContentIfNotNull(root, doc.getExtent(), "extent");
        appendElementWithContentIfNotNull(root, doc.getFinancedFrom(), "financed");
        appendElementWithContentIfNotNull(root, doc.getContractNumber(), "contractNumber");
        //format
        Element formatEl = addElement(root, "format");
        String format = doc.getFormat();
        if (format != null) {
            formatEl.appendChild(format);
        }
        String formatVersion = doc.getFormatVersion();
        if (formatVersion != null) {
            Attribute version = new Attribute("version", formatVersion);
            formatEl.addAttribute(version);
        }
        //resolution
        Element resolutionEl = addElement(root, "resolution");
        appendElementWithContentIfNotNull(resolutionEl, doc.getResolutionWidth(), "width");
        appendElementWithContentIfNotNull(resolutionEl, doc.getResolutionHeight(), "height");
        //compression
        Element compressionEl = addElement(root, "compression");
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
        Element colorEl = addElement(root, "color");
        appendElementWithContentIfNotNull(colorEl, doc.getColorModel(), "model");
        appendElementWithContentIfNotNull(colorEl, doc.getColorDepth(), "depth");
        appendElementWithContentIfNotNull(root, doc.getIccProfile(), "iccProfile");
        //common picture characteristics
        Element pictureEl = addElement(root, "picture");
        appendElementWithContentIfNotNull(pictureEl, doc.getPictureWidth(), "width");
        appendElementWithContentIfNotNull(pictureEl, doc.getPictureHeight(), "height");
        appendBuilderResultfNotNull(root, registrarBuilder);
        appendBuilderResultfNotNull(root, archiverBuilder);
        appendBuilderResultfNotNull(root, entityBuilder);
        if (instanceBuilderList != null) {
            for (DigitalInstanceBuilder builder : instanceBuilderList) {
                appendBuilderResultfNotNull(root, builder);
            }
        }
        return root;
    }
}

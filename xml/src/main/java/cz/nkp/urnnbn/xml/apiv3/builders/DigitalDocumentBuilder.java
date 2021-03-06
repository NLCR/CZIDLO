/*
 * Copyright (C) 2011, 2012 Martin Řehánek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.nkp.urnnbn.xml.apiv3.builders;

import nu.xom.Attribute;
import nu.xom.Element;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;

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

    public DigitalDocumentBuilder(DigitalDocument doc, UrnNbn urn, RegistrarScopeIdentifiersBuilder identifiersBuilder,
            DigitalInstancesBuilder instancesBuilder, RegistrarBuilder registrarBuilder, ArchiverBuilder archiverBuilder,
            IntelectualEntityBuilder entityBuilder) {
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
        Element root = new Element("digitalDocument", CZIDLO_NS);
        root.addAttribute(new Attribute("id", doc.getId().toString()));
        Element urnEl = appendUrnNbnElement(root, urn);
        appendPredecessors(urnEl, urn);
        appendSuccessors(urnEl, urn);
        appendElementWithContentIfNotNull(root, doc.getFinancedFrom(), "financed");
        appendElementWithContentIfNotNull(root, doc.getContractNumber(), "contractNumber");
        appendTimestamps(root, doc, "digital document");
        appendBuilderResultfNotNull(root, entityBuilder);
        appendTechnicalMetadata(root);
        appendBuilderResultfNotNull(root, identifiersBuilder);
        appendBuilderResultfNotNull(root, registrarBuilder);
        appendBuilderResultfNotNull(root, archiverBuilder);
        appendBuilderResultfNotNull(root, instancesBuilder);
        return root;
    }

    private void appendTechnicalMetadata(Element root) {
        Element technicalEl = appendElement(root, "technicalMetadata");
        // format
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
        // extent
        appendElementWithContentIfNotNull(technicalEl, doc.getExtent(), "extent");
        // resolution
        Integer resolutionHorizontal = doc.getResolutionHorizontal();
        Integer resolutionVertical = doc.getResolutionVertical();
        if (resolutionHorizontal != null || resolutionVertical != null) {
            Element resolutionEl = appendElement(technicalEl, "resolution");
            appendElementWithContentIfNotNull(resolutionEl, resolutionHorizontal, "horizontal");
            appendElementWithContentIfNotNull(resolutionEl, resolutionVertical, "vertical");
        }
        // compression
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
        // color
        String colorModel = doc.getColorModel();
        Integer colorDepth = doc.getColorDepth();
        if (colorModel != null || colorDepth != null) {
            Element colorEl = appendElement(technicalEl, "color");
            appendElementWithContentIfNotNull(colorEl, colorModel, "model");
            appendElementWithContentIfNotNull(colorEl, colorDepth, "depth");
        }
        // iccProfile
        appendElementWithContentIfNotNull(technicalEl, doc.getIccProfile(), "iccProfile");
        // picture size
        Integer pictureWidth = doc.getPictureWidth();
        Integer pictureHeight = doc.getPictureHeight();
        if (pictureWidth != null || pictureHeight != null) {
            Element pictureEl = appendElement(technicalEl, "pictureSize");
            appendElementWithContentIfNotNull(pictureEl, pictureWidth, "width");
            appendElementWithContentIfNotNull(pictureEl, pictureHeight, "height");
        }
    }
}

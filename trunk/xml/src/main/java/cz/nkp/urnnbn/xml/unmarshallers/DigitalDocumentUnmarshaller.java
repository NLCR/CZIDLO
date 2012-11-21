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
package cz.nkp.urnnbn.xml.unmarshallers;

import cz.nkp.urnnbn.core.DigDocIdType;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.xml.commons.Xpath;
import cz.nkp.urnnbn.xml.unmarshallers.validation.LimitedLengthEnhancer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nu.xom.Element;
import nu.xom.Nodes;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalDocumentUnmarshaller extends Unmarshaller {

    private final Element digDocEl;

    DigitalDocumentUnmarshaller(Element rootElement) {
        this.digDocEl = rootElement;
    }

    /**
     *
     * @return DigitalDocument object, never null
     */
    DigitalDocument getDigitalDocument() {
        DigitalDocument digDoc = new DigitalDocument();
        digDoc.setFinancedFrom(elementContentOrNull("financed", digDocEl, new LimitedLengthEnhancer(100)));
        digDoc.setContractNumber(elementContentOrNull("contractNumber", digDocEl, new LimitedLengthEnhancer(20)));
        loadTechnicalMetadataToDocument(digDoc);
        return digDoc;
    }

    private void loadTechnicalMetadataToDocument(DigitalDocument digDoc) {
        Element root = selectSingleElementOrNull("technicalMetadata", digDocEl);
        if (root != null) {
            //format
            digDoc.setFormat(elementContentOrNull("format", root, new LimitedLengthEnhancer(20)));
            Element formatEl = selectSingleElementOrNull("format", root);
            digDoc.setFormatVersion(attributeContentOrNull("version", formatEl, new LimitedLengthEnhancer(10)));
            //extent
            digDoc.setExtent(elementContentOrNull("extent", root, new LimitedLengthEnhancer(200)));
            //resolution
            Element resolutionEl = selectSingleElementOrNull("resolution", root);
            if (resolutionEl != null) {
                String resWidthStr = elementContentOrNull("horizontal", resolutionEl, null);
                if (resWidthStr != null) {
                    digDoc.setResolutionHorizontal(Integer.valueOf(resWidthStr));
                }
                String resHeightStr = elementContentOrNull("vertical", resolutionEl, null);
                if (resHeightStr != null) {
                    digDoc.setResolutionVertical(Integer.valueOf(resHeightStr));
                }
            }
            //compression
            digDoc.setCompression(elementContentOrNull("compression", root, new LimitedLengthEnhancer(50)));
            Element compressionEl = selectSingleElementOrNull("compression", root);
            String compressionRatioStr = attributeContentOrNull("ratio", compressionEl, null);
            if (compressionRatioStr != null) {
                digDoc.setCompressionRatio(Double.valueOf(compressionRatioStr));
            }
            //color
            Element colorEl = selectSingleElementOrNull("color", root);
            if (colorEl != null) {
                digDoc.setColorModel(elementContentOrNull("model", colorEl, new LimitedLengthEnhancer(20)));
                String colorDepthStr = elementContentOrNull("depth", colorEl, null);
                if (colorDepthStr != null) {
                    digDoc.setColorDepth(Integer.valueOf(colorDepthStr));
                }
            }
            //ICC profile
            digDoc.setIccProfile(elementContentOrNull("iccProfile", root, new LimitedLengthEnhancer(50)));
            //picture size
            Element pictureEl = selectSingleElementOrNull("pictureSize", root);
            if (pictureEl != null) {
                String picWidthStr = elementContentOrNull("width", pictureEl, null);
                if (picWidthStr != null) {
                    digDoc.setPictureWidth(Integer.valueOf(picWidthStr));
                }
                String picHeightStr = elementContentOrNull("height", pictureEl, null);
                if (picHeightStr != null) {
                    digDoc.setPictureHeight(Integer.valueOf(picHeightStr));
                }
            }
        }
    }

    /**
     *
     * @return list of digital document identifiers, never null
     */
    List<DigDocIdentifier> getDigDocIdentifiers() {
        Element identifiersEl = (Element) selectSingleElementOrNull("registrarScopeIdentifiers", digDocEl);
        if (identifiersEl == null) {
            return Collections.<DigDocIdentifier>emptyList();
        } else {
            Nodes nodes = selectNodes(new Xpath(prefixed("id")), identifiersEl);
            if (nodes.size() == 0) {
                return Collections.<DigDocIdentifier>emptyList();
            } else {
                List<DigDocIdentifier> result = new ArrayList<DigDocIdentifier>(nodes.size());
                for (int i = 0; i < nodes.size(); i++) {
                    Element idEl = (Element) nodes.get(i);
                    String type = idEl.getAttribute("type").getValue();
                    String value = idEl.getValue();
                    DigDocIdentifier id = new DigDocIdentifier();
                    id.setType(DigDocIdType.valueOf(type));
                    id.setValue(value);
                    result.add(id);
                }
                return result;
            }
        }
    }

    /**
     *
     * @return UrnNbn or null
     */
    UrnNbn getUrnNbn() {
        Element urnEl = (Element) selectSingleElementOrNull("urnNbn", digDocEl);
        if (urnEl == null) {
            return null;
        } else {
            return UrnNbn.valueOf(urnEl.getValue());
        }
    }

    /**
     *
     * @return archiver id or null
     */
    Long getArchiverId() {
        Element urnEl = (Element) selectSingleElementOrNull("archiverId", digDocEl);
        if (urnEl == null) {
            return null;
        } else {
            return Long.valueOf(urnEl.getValue());
        }
    }
}

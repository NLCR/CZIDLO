/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.unmarshallers;

import cz.nkp.urnnbn.core.DigDocIdType;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.xml.commons.Xpath;
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
        digDoc.setFinancedFrom(elementContentOrNull("financed", digDocEl));
        digDoc.setFinancedFrom(elementContentOrNull("financed", digDocEl));
        digDoc.setContractNumber(elementContentOrNull("contractNumber", digDocEl));
        loadTechnicalMetadataToDocument(digDoc);
        return digDoc;
    }

    private void loadTechnicalMetadataToDocument(DigitalDocument digDoc) {
        Element root = selectSingleElementOrNull("technicalMetadata", digDocEl);
        if (root == null) {
            logger.severe("element \"technical\" not found");
        } else {
            //format
            digDoc.setFormat(elementContentOrNull("format", root));
            Element formatEl = selectSingleElementOrNull("format", root);
            digDoc.setFormatVersion(attributeContentOrNull("version", formatEl));
            //extent
            digDoc.setExtent(elementContentOrNull("extent", root));
            //resolution
            Element resolutionEl = selectSingleElementOrNull("resolution", root);
            String resWidthStr = elementContentOrNull("horizontal", resolutionEl);
            if (resWidthStr != null) {
                digDoc.setResolutionHorizontal(Integer.valueOf(resWidthStr));
            }
            String resHeightStr = elementContentOrNull("vertical", resolutionEl);
            if (resHeightStr != null) {
                digDoc.setResolutionVertical(Integer.valueOf(resHeightStr));
            }
            //compression
            digDoc.setCompression(elementContentOrNull("compression", root));
            Element compressionEl = selectSingleElementOrNull("compression", root);
            String compressionRatioStr = attributeContentOrNull("ratio", compressionEl);
            if (compressionRatioStr != null) {
                digDoc.setCompressionRatio(Double.valueOf(compressionRatioStr));
            }
            //color
            Element colorEl = selectSingleElementOrNull("color", root);
            if (colorEl != null) {
                digDoc.setColorModel(elementContentOrNull("model", colorEl));
                String colorDepthStr = elementContentOrNull("depth", colorEl);
                if (colorDepthStr != null) {
                    digDoc.setColorDepth(Integer.valueOf(colorDepthStr));
                }
            }
            //ICC profile
            digDoc.setIccProfile(elementContentOrNull("iccProfile", root));
            //picture size
            Element pictureEl = selectSingleElementOrNull("pictureSize", root);
            if (pictureEl != null) {
                String picWidthStr = elementContentOrNull("width", pictureEl);
                if (picWidthStr != null) {
                    digDoc.setPictureWidth(Integer.valueOf(picWidthStr));
                }
                String picHeightStr = elementContentOrNull("height", pictureEl);
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter.czidloapi.utils;

import cz.nkp.urnnbn.oaiadapter.czidloapi.CzidloApiConnector;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

/**
 * @author Jan Rychtář
 * @author Martin Řehánek
 */
public class DdRegistrationRefiner extends XmlRefiner {

    public void refineDocument(Document document) {
        Element root = document.getRootElement();
        if ("import".equals(root.getLocalName())) {
            refineImportElement(root);
        }
    }

    private void refineImportElement(Element importElement) {
        Elements importChildren = importElement.getChildElements();
        if (importChildren.size() > 0) {
            Element entityElement = importChildren.get(0);
            String entityName = entityElement.getLocalName();
            if ("monograph".equals(entityName) || "monographVolume".equals(entityName) || "periodical".equals(entityName)
                    || "periodicalVolume".equals(entityName) || "periodicalIssue".equals(entityName) || "analytical".equals(entityName)
                    || "thesis".equals(entityName) || "otherEntity".equals(entityName)) {
                refineEntityElement(entityElement);
            }
        }
        Element digitalDocumentElement = importElement.getFirstChildElement("digitalDocument", CzidloApiConnector.CZIDLO_NAMESPACE);
        if (digitalDocumentElement != null) {
            refineDigitalDocumentElement(digitalDocumentElement);
        }
    }

    private void refineEntityElement(Element entityElement) {
        Element titleInfoElement = entityElement.getFirstChildElement("titleInfo", CzidloApiConnector.CZIDLO_NAMESPACE);
        if (titleInfoElement != null) {
            refineTitleInfoElement(titleInfoElement);
        }
        removeElementIfContentNoMatch(entityElement, "ccnb", "cnb\\d{9}|CNB\\d{9}");
        removeElementIfContentNoMatch(
                entityElement,
                "isbn",
                "(978){0,1}80\\d([0-9]|){6}\\d[0-9xX]|(978-){0,1}80-\\d([0-9]|-){6}\\d-[0-9xX]|(978\\s){0,1}80\\s\\d([0-9]|\\s){6}\\d\\s[0-9xX]|978-80\\d([0-9]|){6}\\d[0-9xX]");
        removeElementIfContentNoMatch(entityElement, "issn", "\\d{4}-\\d{3}[0-9Xx]{1}");
        cutContentIfToLongRemoveElementIfToShort(entityElement, "otherId", 50, 1);
        cutContentIfToLongRemoveElementIfToShort(entityElement, "documentType", 50, 0);
        cutContentIfToLongRemoveElementIfToShort(entityElement, "otherOriginator", 50, 0);
        removeElementIfContentNoMatch(entityElement, "digitalBorn", "true|false|0|1");
        cutContentIfToLongRemoveElementIfToShort(entityElement, "degreeAwardingInstitution", 50, 0);

        Element publicationElement = entityElement.getFirstChildElement("publication", CzidloApiConnector.CZIDLO_NAMESPACE);
        if (publicationElement != null) {
            refinePublicationElement(publicationElement);
        }
        Element sourceDocumentElement = entityElement.getFirstChildElement("sourceDocument", CzidloApiConnector.CZIDLO_NAMESPACE);
        if (sourceDocumentElement != null) {
            refineEntityElement(sourceDocumentElement);
        }
    }

    private void refineTitleInfoElement(Element titleInfoElement) {
        cutContentIfToLongRemoveElementIfToShort(titleInfoElement, "title", 100, 1);
        cutContentIfToLongRemoveElementIfToShort(titleInfoElement, "subTitle", 200, 1);
        cutContentIfToLongRemoveElementIfToShort(titleInfoElement, "monographTitle", 100, 1);
        cutContentIfToLongRemoveElementIfToShort(titleInfoElement, "volumeTitle", 50, 0);
        cutContentIfToLongRemoveElementIfToShort(titleInfoElement, "periodicalTitle", 100, 1);
        cutContentIfToLongRemoveElementIfToShort(titleInfoElement, "issueTitle", 50, 0);
    }

    private void refinePublicationElement(Element publicationElement) {
        cutContentIfToLongRemoveElementIfToShort(publicationElement, "publisher", 50, 0);
        cutContentIfToLongRemoveElementIfToShort(publicationElement, "place", 50, 0);
        removeElementIfContentNoMatch(publicationElement, "year", "\\d{1,4}");
    }

    private void refineDigitalDocumentElement(Element digitalDocumentElement) {
        Element registrarScopeIdentifiersEl = digitalDocumentElement.getFirstChildElement("registrarScopeIdentifiers", CzidloApiConnector.CZIDLO_NAMESPACE);
        if (registrarScopeIdentifiersEl != null) {
            refineRegistrarScopeIdentifiers(registrarScopeIdentifiersEl);
        }
        cutContentIfToLongRemoveElementIfToShort(digitalDocumentElement, "financed", 100, 1);
        Element technicalMetadataElement = digitalDocumentElement.getFirstChildElement("technicalMetadata", CzidloApiConnector.CZIDLO_NAMESPACE);
        if (technicalMetadataElement != null) {
            refineTechnicalMetadataElement(technicalMetadataElement);
        }
    }

    private void refineRegistrarScopeIdentifiers(Element registrarScopeIdentifiersEl) {
        Elements registrarScopeIdentifierEls = registrarScopeIdentifiersEl.getChildElements("id", CzidloApiConnector.CZIDLO_NAMESPACE);
        for (int i = 0; i < registrarScopeIdentifierEls.size(); i++) {
            Element registrarScopeIdentifierEl = registrarScopeIdentifierEls.get(i);
            refineRegistrarScopeIdentifier(registrarScopeIdentifierEl, registrarScopeIdentifiersEl);
        }
    }

    private void refineRegistrarScopeIdentifier(Element registrarScopeIdentifierEl, Element parentEl) {
        String typeRegexp = "[A-Za-z0-9]{1}[A-Za-z0-9_\\-:]{0,18}[A-Za-z0-9]{1}";
        String valueRegexp = "[A-Za-z0-9]{1}[A-Za-z0-9\\-_\\.~!\\*'\\(\\);:@&=+$,\\?#\\[\\]]{0,58}[A-Za-z0-9]{1}|[A-Za-z0-9]{1}";
        removeAttributeIfValueNoMatch(registrarScopeIdentifierEl, "previousValue", valueRegexp);
        boolean typeRemoved = removeAttributeIfValueNoMatch(registrarScopeIdentifierEl, "type", typeRegexp);
        if (typeRemoved) {
            parentEl.removeChild(registrarScopeIdentifierEl);
        } else {
            removeElementIfContentNoMatch(parentEl, registrarScopeIdentifierEl, valueRegexp);
        }
    }

    private void refineTechnicalMetadataElement(Element technicalMetadataElement) {
        cutContentIfToLongRemoveElementIfToShort(technicalMetadataElement, "format", 20, 1);
        cutContentIfToLongRemoveElementIfToShort(technicalMetadataElement, "extent", 200, 1);
        Element resolutionElement = technicalMetadataElement.getFirstChildElement("resolution", CzidloApiConnector.CZIDLO_NAMESPACE);
        if (resolutionElement != null) {
            refineResolutionElement(resolutionElement, technicalMetadataElement);
        }
        cutContentIfToLongRemoveElementIfToShort(technicalMetadataElement, "compression", 50, 1);
        Element colorElement = technicalMetadataElement.getFirstChildElement("color", CzidloApiConnector.CZIDLO_NAMESPACE);
        if (colorElement != null) {
            refineColorElement(colorElement);
        }
        cutContentIfToLongRemoveElementIfToShort(technicalMetadataElement, "iccProfile", 50, 1);
        Element pictureSizenElement = technicalMetadataElement.getFirstChildElement("pictureSize", CzidloApiConnector.CZIDLO_NAMESPACE);
        if (pictureSizenElement != null) {
            refinePictureSizeElement(pictureSizenElement, technicalMetadataElement);
        }
    }

    private void refineColorElement(Element colorElement) {
        cutContentIfToLongRemoveElementIfToShort(colorElement, "model", 20, 1);
        removeElementIfContentNoMatch(colorElement, "depth", "\\d*");
    }

    private void refineResolutionElement(Element resolutionElement, Element parentElement) {
        boolean hRemoved = removeElementIfContentNoMatch(resolutionElement, "horizontal", "\\d*");
        boolean vRemoved = removeElementIfContentNoMatch(resolutionElement, "vertical", "\\d*");
        if (hRemoved || vRemoved) {
            parentElement.removeChild(resolutionElement);
        }
    }

    private void refinePictureSizeElement(Element pictureSizenElement, Element parentElement) {
        boolean wRemoved = removeElementIfContentNoMatch(pictureSizenElement, "width", "\\d*");
        boolean hRemoved = removeElementIfContentNoMatch(pictureSizenElement, "height", "\\d*");
        if (wRemoved || hRemoved) {
            parentElement.removeChild(pictureSizenElement);
        }
    }

}

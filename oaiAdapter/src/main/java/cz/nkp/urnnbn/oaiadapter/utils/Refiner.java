/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter.utils;

import cz.nkp.urnnbn.oaiadapter.DocumentOperationException;
import cz.nkp.urnnbn.oaiadapter.czidlo.CzidloApiConnector;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jan Rychtář
 * @author Martin Řehánek
 */
public class Refiner {

    // TODO: 30.10.17 either frow away or properly handle double xsd validation, certainly not with just log if doc is invalid after this
    public static Document refineDocument(Document document, String xsd) {
        Element root = document.getRootElement();
        if ("import".equals(root.getLocalName())) {
            refineImportElement(root);
        }
        try {
            XmlTools.validateByXsdAsString(document, xsd);
        } catch (DocumentOperationException ex) {
            Logger.getLogger(Refiner.class.getName()).log(Level.SEVERE, null, ex);
        }
        return document;
    }

    private static void refineImportElement(Element importElement) {
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

    private static void refineEntityElement(Element entityElement) {
        Element titleInfoElement = entityElement.getFirstChildElement("titleInfo", CzidloApiConnector.CZIDLO_NAMESPACE);
        if (titleInfoElement != null) {
            refineTitleInfoElement(titleInfoElement);
        }
        removeIfNoMatch(entityElement, "ccnb", "cnb\\d{9}|CNB\\d{9}");
        removeIfNoMatch(
                entityElement,
                "isbn",
                "(978){0,1}80\\d([0-9]|){6}\\d[0-9xX]|(978-){0,1}80-\\d([0-9]|-){6}\\d-[0-9xX]|(978\\s){0,1}80\\s\\d([0-9]|\\s){6}\\d\\s[0-9xX]|978-80\\d([0-9]|){6}\\d[0-9xX]");
        removeIfNoMatch(entityElement, "issn", "\\d{4}-\\d{3}[0-9Xx]{1}");
        cutIfToLongRemoveIfToShort(entityElement, "otherId", 50, 1);
        cutIfToLongRemoveIfToShort(entityElement, "documentType", 50, 0);
        // TODO:primaryOriginator
        cutIfToLongRemoveIfToShort(entityElement, "otherOriginator", 50, 0);
        removeIfNoMatch(entityElement, "digitalBorn", "true|false|0|1");
        cutIfToLongRemoveIfToShort(entityElement, "degreeAwardingInstitution", 50, 0);

        Element publicationElement = entityElement.getFirstChildElement("publication", CzidloApiConnector.CZIDLO_NAMESPACE);
        if (publicationElement != null) {
            refinePublicationElement(publicationElement);
        }
        Element sourceDocumentElement = entityElement.getFirstChildElement("sourceDocument", CzidloApiConnector.CZIDLO_NAMESPACE);
        if (sourceDocumentElement != null) {
            refineEntityElement(sourceDocumentElement);
        }
    }

    private static void refineTitleInfoElement(Element titleInfoElement) {
        cutIfToLongRemoveIfToShort(titleInfoElement, "title", 100, 1);
        cutIfToLongRemoveIfToShort(titleInfoElement, "subTitle", 200, 1);
        cutIfToLongRemoveIfToShort(titleInfoElement, "monographTitle", 100, 1);
        cutIfToLongRemoveIfToShort(titleInfoElement, "volumeTitle", 50, 0);
        cutIfToLongRemoveIfToShort(titleInfoElement, "periodicalTitle", 100, 1);
        cutIfToLongRemoveIfToShort(titleInfoElement, "issueTitle", 50, 0);
    }

    private static void refinePublicationElement(Element publicationElement) {
        cutIfToLongRemoveIfToShort(publicationElement, "publisher", 50, 0);
        cutIfToLongRemoveIfToShort(publicationElement, "place", 50, 0);
        removeIfNoMatch(publicationElement, "year", "\\d{1,4}");
    }

    private static void refineUrnNbnElement(Element urnNbnElement) {
        removeIfNoMatch(urnNbnElement, "value", "urn:nbn:cz:[A-Za-z0-9]{2,6}\\-[A-Za-z0-9]{6}");
    }

    private static void refineDigitalDocumentElement(Element digitalDocumentElement) {
        removeIfNoMatch(digitalDocumentElement, "archiverId", "\\d*");

        Element urnNbnElement = digitalDocumentElement.getFirstChildElement("urnNbn", CzidloApiConnector.CZIDLO_NAMESPACE);
        if (urnNbnElement != null) {
            refineUrnNbnElement(urnNbnElement);
        }
        // removeIfNoMatch(digitalDocumentElement, "urnNbn", "urn:nbn:cz:[A-Za-z0-9]{2,6}\\-[A-Za-z0-9]{6}");
        // TODO: registrarScopeIdentifiers
        cutIfToLongRemoveIfToShort(digitalDocumentElement, "financed", 100, 1);
        Element technicalMetadataElement = digitalDocumentElement.getFirstChildElement("technicalMetadata", CzidloApiConnector.CZIDLO_NAMESPACE);
        if (technicalMetadataElement != null) {
            refineTechnicalMetadataElement(technicalMetadataElement);
        }
    }

    private static void refineTechnicalMetadataElement(Element technicalMetadataElement) {
        cutIfToLongRemoveIfToShort(technicalMetadataElement, "format", 20, 1);
        cutIfToLongRemoveIfToShort(technicalMetadataElement, "extent", 200, 1);
        Element resolutionElement = technicalMetadataElement.getFirstChildElement("resolution", CzidloApiConnector.CZIDLO_NAMESPACE);
        if (resolutionElement != null) {
            refineResolutionElement(resolutionElement, technicalMetadataElement);
        }
        cutIfToLongRemoveIfToShort(technicalMetadataElement, "compression", 50, 1);
        Element colorElement = technicalMetadataElement.getFirstChildElement("color", CzidloApiConnector.CZIDLO_NAMESPACE);
        if (colorElement != null) {
            refineColorElement(colorElement);
        }
        cutIfToLongRemoveIfToShort(technicalMetadataElement, "iccProfile", 50, 1);
        Element pictureSizenElement = technicalMetadataElement.getFirstChildElement("pictureSize", CzidloApiConnector.CZIDLO_NAMESPACE);
        if (pictureSizenElement != null) {
            refinePictureSizeElement(pictureSizenElement, technicalMetadataElement);
        }
    }

    private static void refineColorElement(Element colorElement) {
        cutIfToLongRemoveIfToShort(colorElement, "model", 20, 1);
        removeIfNoMatch(colorElement, "depth", "\\d*");
    }

    private static void refineResolutionElement(Element resolutionElement, Element parentElement) {
        boolean hRemoved = removeIfNoMatch(resolutionElement, "horizontal", "\\d*");
        boolean vRemoved = removeIfNoMatch(resolutionElement, "vertical", "\\d*");
        // TODO: 30.10.17 should be && not ||
        if (hRemoved || vRemoved) {
            parentElement.removeChild(resolutionElement);
        }
    }

    private static void refinePictureSizeElement(Element pictureSizenElement, Element parentElement) {
        boolean wRemoved = removeIfNoMatch(pictureSizenElement, "width", "\\d*");
        boolean hRemoved = removeIfNoMatch(pictureSizenElement, "height", "\\d*");
        // TODO: 30.10.17 should be && not ||
        if (wRemoved || hRemoved) {
            parentElement.removeChild(pictureSizenElement);
        }
    }

    private static void cutIfToLongRemoveIfToShort(Element parent, String name, int maxLength, int minLength) {
        Element el = parent.getFirstChildElement(name, CzidloApiConnector.CZIDLO_NAMESPACE);
        if (el != null) {
            String value = el.getValue();
            if (value.length() > maxLength) {
                el.removeChildren();
                el.appendChild(value.substring(0, maxLength));
            }
            if (value.length() < minLength) {
                parent.removeChild(el);
            }
        }
    }

    private static boolean removeIfNoMatch(Element parent, String name, String regex) {
        Element el = parent.getFirstChildElement(name, CzidloApiConnector.CZIDLO_NAMESPACE);
        if (el != null) {
            String value = el.getValue();
            if (!value.matches(regex)) {
                parent.removeChild(el);
                return true;
            }
        }
        return false;
    }
}

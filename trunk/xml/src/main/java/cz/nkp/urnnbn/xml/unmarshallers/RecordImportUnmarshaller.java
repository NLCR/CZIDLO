/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.unmarshallers;

import nu.xom.Document;
import cz.nkp.urnnbn.core.DigDocIdType;
import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.OriginType;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.xml.commons.Xpath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

/**
 *
 * @author Martin Řehánek
 */
public class RecordImportUnmarshaller extends Unmarshaller {

    private final Element entityEl;
    private final Element digDocEl;
    //TODO: pokud bude potreba, dat xpathy do statickych final atributu
    //(ty stringy se totiz buduji vzdy znovu)
    private static final Xpath DIG_REP_XPATH =
            new Xpath('/' + prefixed("import") + '/' + prefixed("digitalDocument"));

    public RecordImportUnmarshaller(Document doc) {
        super(doc);
        entityEl = intelectualEntityElement();
        digDocEl = (Element) selectSingleElementOrNullFromdoc(DIG_REP_XPATH);
    }

    /**
     * 
     * @return Element or null. Null can only be returned if the inetelectual entity type is unknown.
     * It is expected here that import xml is validated agains xsd so that this should never happen.
     */
    private Element intelectualEntityElement() {
        for (EntityType type : EntityType.values()) {
            Node node = selectSingleElementOrNullFromdoc(
                    new Xpath('/' + prefixed("import") + '/' + prefixed(type.toString())));
            if (node != null) {
                return (Element) node;
            }
        }
        return null;
    }

    /**
     * 
     * @return IntelectualEntity object, never null
     */
    public IntelectualEntity getIntelectualEntity() {
        EntityType type = toEntityType(entityEl);
        String title = elementContentOrNull("title", entityEl);
        String subTitle = elementContentOrNull("subTitle", entityEl);
        String digitalBornStr = elementContentOrNull("digitalBorn", entityEl);
        boolean digitalBorn = digitalBornStr == null
                ? false : Boolean.valueOf(digitalBornStr);
        String degreeAwardingInst = elementContentOrNull("degreeAwardingInstitution", entityEl);
        String documentType = elementContentOrNull("documentType", entityEl);

        IntelectualEntity result = new IntelectualEntity();
        result.setEntityType(type);
        result.setTitle(title);
        result.setAlternativeTitle(subTitle);
        result.setDigitalBorn(digitalBorn);
        result.setDegreeAwardingInstitution(degreeAwardingInst);
        result.setDocumentType(documentType);
        return result;
    }

    private EntityType toEntityType(Element entityEl) {
        String localName = entityEl.getLocalName();
        for (EntityType type : EntityType.values()) {
            if (localName.equals(type.toString())) {
                return type;
            }
        }
        return null;
    }

    /**
     * 
     * @return Publication object or null
     */
    public Publication getPublication() {
        String publisher = elementContentOrNull("publisher", entityEl);
        String place = elementContentOrNull("publicationPlace", entityEl);
        String yearStr = elementContentOrNull("publicationYear", entityEl);
        if (publisher == null && place == null && yearStr == null) {
            return null;
        } else {
            Publication result = new Publication();
            result.setPublisher(publisher);
            result.setPlace(place);
            if (yearStr != null) {
                result.setYear(Integer.valueOf(yearStr));
            }
            return result;
        }
    }

    /**
     * 
     * @return Originator object or null
     */
    public Originator getOriginator() {
        Element originator = (Element) selectSingleElementOrNull("originator", entityEl);
        if (originator != null) {
            String typStr = originator.getAttribute("type").getValue();
            OriginType type = OriginType.valueOf(typStr);
            String value = originator.getValue();
            Originator result = new Originator();
            result.setType(type);
            result.setValue(value);
            return result;
        } else {
            return null;
        }
    }

    /**
     * 
     * @return SourceDocument object or null
     */
    public SourceDocument getSourceDocument() {
        Element sourceDoc = (Element) selectSingleElementOrNull("sourceDocument", entityEl);
        if (sourceDoc != null) {
            String title = elementContentOrNull("title", sourceDoc);
            String ccnb = elementContentOrNull("ccnb", sourceDoc);
            String isbn = elementContentOrNull("isbn", sourceDoc);
            String issn = elementContentOrNull("issn", sourceDoc);
            String otherId = elementContentOrNull("otherId", sourceDoc);
            String perVolume = elementContentOrNull("perVolume", sourceDoc);
            String perIssue = elementContentOrNull("perIssue", sourceDoc);
            String publisher = elementContentOrNull("publisher", sourceDoc);
            String publicationPlace = elementContentOrNull("publicationPlace", sourceDoc);
            String publicationYearStr = elementContentOrNull("publicationYear", sourceDoc);
            int publicationYear = Integer.valueOf(publicationYearStr);
            SourceDocument result = new SourceDocument();
            result.setTitle(title);
            result.setCcnb(ccnb);
            result.setIsbn(isbn);
            result.setIssn(issn);
            result.setOtherId(otherId);
            result.setPeriodicalVolume(perVolume);
            result.setPeriodicalNumber(perIssue);
            result.setPublisher(publisher);
            result.setPublicationPlace(publicationPlace);
            result.setPublicationYear(publicationYear);
            return result;
        } else {
            return null;
        }
    }

    public List<IntEntIdentifier> getIntEntIdentifiers() {
        List<IntEntIdentifier> result = new ArrayList<IntEntIdentifier>();
        for (IntEntIdType type : IntEntIdType.values()) {
            String elementName = type.toString();
            String idValue = elementContentOrNull(elementName, entityEl);
            if (idValue != null) {
                IntEntIdentifier id = new IntEntIdentifier();
                id.setType(type);
                id.setValue(idValue);
                result.add(id);
            }
        }
        return result;
    }

    /**
     * 
     * @return DigitalDocument object, never null
     */
    public DigitalDocument getDigitalDocument() {
        DigitalDocument digDoc = new DigitalDocument();
        digDoc.setFinancedFrom(elementContentOrNull("financed", digDocEl));
        digDoc.setExtent(elementContentOrNull("extent", digDocEl));
        digDoc.setFinancedFrom(elementContentOrNull("financed", digDocEl));
        digDoc.setContractNumber(elementContentOrNull("contractNumber", digDocEl));
        //format
        digDoc.setFormat(elementContentOrNull("format", digDocEl));
        Element formatEl = selectSingleElementOrNull("format", digDocEl);
        digDoc.setFormatVersion(attributeContentOrNull("version", formatEl));
        //resolution
        Element resolutionEl = selectSingleElementOrNull("resolution", digDocEl);
        String resWidthStr = elementContentOrNull("width", resolutionEl);
        if (resWidthStr != null) {
            digDoc.setResolutionWidth(Integer.valueOf(resWidthStr));
        }
        String resHeightStr = elementContentOrNull("height", resolutionEl);
        if (resHeightStr != null) {
            digDoc.setResolutionHeight(Integer.valueOf(resHeightStr));
        }
        //compression
        digDoc.setCompression(elementContentOrNull("compression", digDocEl));
        Element compressionEl = selectSingleElementOrNull("compression", digDocEl);
        String compressionRatioStr = attributeContentOrNull("ratio", compressionEl);
        if (compressionRatioStr != null) {
            digDoc.setCompressionRatio(Double.valueOf(compressionRatioStr));
        }
        //color
        Element colorEl = selectSingleElementOrNull("color", digDocEl);
        digDoc.setColorModel(elementContentOrNull("model", colorEl));
        String colorDepthStr = elementContentOrNull("depth", colorEl);
        if (colorDepthStr != null) {
            digDoc.setColorDepth(Integer.valueOf(colorDepthStr));
        }
        //ICC profile
        digDoc.setIccProfile(elementContentOrNull("iccProfile", digDocEl));
        //picture
        Element pictureEl = selectSingleElementOrNull("picture", digDocEl);
        String picWidthStr = elementContentOrNull("width", pictureEl);
        if (picWidthStr != null) {
            digDoc.setPictureWidth(Integer.valueOf(picWidthStr));
        }
        String picHeightStr = elementContentOrNull("height", pictureEl);
        if (picHeightStr != null) {
            digDoc.setPictureHeight(Integer.valueOf(picHeightStr));
        }
        return digDoc;
    }

    public List<DigDocIdentifier> getDigRepIdentifiers() {
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
    public UrnNbn getUrnNbn() {
        Element urnEl = (Element) selectSingleElementOrNull("urnNbn", digDocEl);
        if (urnEl == null) {
            return null;
        } else {
            return UrnNbn.valueOf(urnEl.getValue());
        }
    }

    public Long getArchiverId() {
        Element urnEl = (Element) selectSingleElementOrNull("archiverId", digDocEl);
        if (urnEl == null) {
            return null;
        } else {
            return Long.valueOf(urnEl.getValue());
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.unmarshallers;

import nu.xom.Document;
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
import java.util.List;
import nu.xom.Element;
import nu.xom.Node;

/**
 *
 * @author Martin Řehánek
 */
public class RecordImportUnmarshaller extends Unmarshaller {

    private final Document doc;
    private final Element entityEl;
    private final DigitalDocumentUnmarshaller digDocUnmarshaller;
    //TODO: pokud bude potreba, dat xpathy do statickych final atributu
    //(ty stringy se totiz buduji vzdy znovu)
    private static final Xpath DIG_REP_XPATH =
            new Xpath('/' + prefixed("import") + '/' + prefixed("digitalDocument"));

    public RecordImportUnmarshaller(Document doc) {
        this.doc = doc;
        entityEl = intelectualEntityElement();
        Element digitalDocumentEl = (Element) selectSingleElementOrNullFromdoc(DIG_REP_XPATH);
        digDocUnmarshaller = new DigitalDocumentUnmarshaller(digitalDocumentEl);
    }

    private Element selectSingleElementOrNullFromdoc(Xpath xpath) {
        return (Element) selectSingleNodeOrNull(xpath, doc);
    }

    /**
     * 
     * @return Element or null. Null can only be returned if the inetelectual entity type is unknown.
     * It is expected here that import xml is validated against xsd so that this should never happen.
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
        Element publicationEl = selectSingleElementOrNull("publication", entityEl);
        if (publicationEl != null) {
            String publisher = elementContentOrNull("publisher", publicationEl);
            String place = elementContentOrNull("place", publicationEl);
            String yearStr = elementContentOrNull("year", publicationEl);
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
        } else {
            return null;
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
            SourceDocument result = new SourceDocument();
            result.setTitle(elementContentOrNull("title", sourceDoc));
            result.setCcnb(elementContentOrNull("ccnb", sourceDoc));
            result.setIsbn(elementContentOrNull("isbn", sourceDoc));
            result.setIssn(elementContentOrNull("issn", sourceDoc));
            result.setOtherId(elementContentOrNull("otherId", sourceDoc));
            result.setPeriodicalVolume(elementContentOrNull("perVolume", sourceDoc));
            result.setPeriodicalNumber(elementContentOrNull("perIssue", sourceDoc));
            Element publicationEl = selectSingleElementOrNull("publication", sourceDoc);
            if (publicationEl != null) {
                result.setPublisher(elementContentOrNull("publisher", publicationEl));
                result.setPublicationPlace(elementContentOrNull("place", publicationEl));
                String yearStr = elementContentOrNull("year", publicationEl);
                result.setPublicationYear(Integer.valueOf(yearStr));
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * 
     * @return list of intelectual entity identifiers, never null
     */
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
        return digDocUnmarshaller.getDigitalDocument();
    }

    /**
     * 
     * @return list of digital document identifiers, never null
     */
    public List<DigDocIdentifier> getDigRepIdentifiers() {
        return digDocUnmarshaller.getDigDocIdentifiers();
    }

    /**
     * 
     * @return UrnNbn or null
     */
    public UrnNbn getUrnNbn() {
        return digDocUnmarshaller.getUrnNbn();
    }

    /**
     * 
     * @return archiver id or null
     */
    public Long getArchiverId() {
        return digDocUnmarshaller.getArchiverId();
    }
}

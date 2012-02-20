/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Attribute;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public abstract class IntelectualEntityBuilder extends XmlBuilder {

    private static final Logger logger = Logger.getLogger(IntelectualEntityBuilder.class.getName());

    public static IntelectualEntityBuilder instanceOf(IntelectualEntity entity, List<IntEntIdentifier> ieIdentfiers, Publication pub, Originator originator, SourceDocument srcDoc) {
        EntityType entityType = entity.getEntityType();
        switch (entityType) {
            case MONOGRAPH:
                return new MonographBuilder(entity, ieIdentfiers, pub, originator, srcDoc);
            case MONOGRAPH_VOLUME:
                return new MonographVolumeBuilder(entity, ieIdentfiers, pub, originator, srcDoc);
            case PERIODICAL:
                return new PeriodicalBuilder(entity, ieIdentfiers, pub, originator, srcDoc);
            case PERIODICAL_VOLUME:
                return new PeriodicalVolumeBuilder(entity, ieIdentfiers, pub, originator, srcDoc);
            case PERIODICAL_ISSUE:
                return new PeriodicalIssueBuilder(entity, ieIdentfiers, pub, originator, srcDoc);
            case THESIS:
                return new ThesisBuilder(entity, ieIdentfiers, pub, originator, srcDoc);
            case ANALYTICAL:
                return new AnalyticalBuilder(entity, ieIdentfiers, pub, originator, srcDoc);
            case OTHER:
                return new OtherEntityBuilder(entity, ieIdentfiers, pub, originator, srcDoc);
            default:
                return null;
        }
    }
    protected final IntelectualEntity entity;
    protected final List<IntEntIdentifier> identifiers;
    protected final Publication publication;
    protected final Originator originator;
    protected final SourceDocument srcDoc;

    public IntelectualEntityBuilder(IntelectualEntity entity, List<IntEntIdentifier> identifiers, Publication publication, Originator originator, SourceDocument srcDoc) {
        this.entity = entity;
        this.identifiers = identifiers;
        this.publication = publication;
        this.originator = originator;
        this.srcDoc = srcDoc;
    }

    Element entityElement() {
        Element result = new Element("intelectualEntity", RESOLVER);
        EntityType entityType = entity.getEntityType();
        result.addAttribute(new Attribute("type", entityType.name()));
        return result;
    }

    void appendCreatedAndUpdated(Element root) {
        Element created = appendElementWithContentIfNotNull(root, entity.getCreated(), "created");
        if (created == null) {
            logger.log(Level.WARNING, "empty value of \"created\" for intelectual entity {0}", entity.getId());
        }
        Element updated = appendElementWithContentIfNotNull(root, entity.getLastUpdated(), "lastUpdated");
        if (updated == null) {
            logger.log(Level.WARNING, "empty value of \"updated\" for intelectual entity {0}", entity.getId());
        }
    }

    void appendTitleAndSubtitle(Element root) {
        Element titleEl = appendElementWithContentIfNotNull(root, entity.getTitle(), "title");
        if (titleEl == null) {
            logger.log(Level.WARNING, "empty value of \"title\" for intelectual entity {0}", entity.getId());
        }
        appendElementWithContentIfNotNull(root, entity.getAlternativeTitle(), "subTitle");
    }

    void appendDocumentType(Element root) {
        appendElementWithContentIfNotNull(root, entity.getDocumentType(), "documentType");
    }

    void appendDigitalBorn(Element root) {
        Element digitalBorn = appendElementWithContentIfNotNull(root, entity.isDigitalBorn(), "digitalBorn");
        if (digitalBorn == null) {
            logger.log(Level.WARNING, "empty value of \"digitalBorn\" for entity {0}", entity.getId());
        }
    }

    void appendOriginator(Element root) {
        if (originator != null) {
            Element originatorEl = addElement(root, "originator");
            Attribute type = new Attribute("type", originator.getType().name());
            originatorEl.addAttribute(type);
            originatorEl.appendChild(originator.getValue());
        } else {
            logger.log(Level.WARNING, "empty value of \"originator\" for entity {0}", entity.getId());
        }

    }

    void appendPublication(Element root) {
        if (publication != null) {
            Element pubEl = addElement(root, "publication");
            appendElementWithContentIfNotNull(pubEl, publication.getPublisher(), "publisher");
            appendElementWithContentIfNotNull(pubEl, publication.getPlace(), "place");
            appendElementWithContentIfNotNull(pubEl, publication.getYear(), "year");
        }
    }

    void appendSourceDocument(Element root) {
        if (srcDoc != null) {
            Element srcDocEl = addElement(root, "sourceDocument");
            appendElementWithContentIfNotNull(srcDocEl, srcDoc.getTitle(), "title");
            appendElementWithContentIfNotNull(srcDocEl, srcDoc.getCcnb(), "ccnb");
            appendElementWithContentIfNotNull(srcDocEl, srcDoc.getIsbn(), "isbn");
            appendElementWithContentIfNotNull(srcDocEl, srcDoc.getIssn(), "issn");
            appendElementWithContentIfNotNull(srcDocEl, srcDoc.getOtherId(), "otherId");
            appendElementWithContentIfNotNull(srcDocEl, srcDoc.getPeriodicalVolume(), "periodicalVolume");
            appendElementWithContentIfNotNull(srcDocEl, srcDoc.getPeriodicalNumber(), "periodicalNumber");
            appendElementWithContentIfNotNull(srcDocEl, srcDoc.getPublisher(), "publisher");
            appendElementWithContentIfNotNull(srcDocEl, srcDoc.getPublicationPlace(), "publicationPlace");
            appendElementWithContentIfNotNull(srcDocEl, srcDoc.getPublicationYear(), "publicationYear");
        } else {
            logger.log(Level.WARNING, "empty value of \"source document\" for entity {0}", entity.getId());
        }
    }

    void appendAgreeAwardingInstitution(Element root) {
        Element el = appendElementWithContentIfNotNull(root, entity.getDegreeAwardingInstitution(), "degreeAwardingInstitution");
    }

    void appendEntityIdentifier(Element root, String string) {
        if (identifiers != null) {
            for (IntEntIdentifier identifier : identifiers) {
                String idType = identifier.getType().toString();
                if (string.equals(idType)) {
                    Element idElement = addElement(root, idType);
                    idElement.appendChild(identifier.getValue());
                }
            }
        }
    }
}

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
package cz.nkp.urnnbn.xml.apiv4.builders;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import nu.xom.Attribute;
import nu.xom.Element;
import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.SourceDocument;

/**
 *
 * @author Martin Řehánek
 */
public abstract class IntelectualEntityBuilder extends XmlBuilder {

    private static final Logger logger = Logger.getLogger(IntelectualEntityBuilder.class.getName());

    public static IntelectualEntityBuilder instanceOf(IntelectualEntity entity, List<IntEntIdentifier> ieIdentfiers, Publication pub,
            Originator originator, SourceDocument srcDoc) {
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
            throw new RuntimeException();
        }
    }

    protected final IntelectualEntity entity;
    protected final List<IntEntIdentifier> identifiers;
    protected final Publication publication;
    protected final Originator originator;
    protected final SourceDocument srcDoc;
    private final Map<IntEntIdType, String> intEntIdMap = new EnumMap<IntEntIdType, String>(IntEntIdType.class);

    public IntelectualEntityBuilder(IntelectualEntity entity, List<IntEntIdentifier> identifiers, Publication publication, Originator originator,
            SourceDocument srcDoc) {
        this.entity = entity;
        this.identifiers = identifiers;
        this.publication = publication;
        this.originator = originator;
        this.srcDoc = srcDoc;
        if (identifiers != null) {
            for (IntEntIdentifier identifier : identifiers) {
                intEntIdMap.put(identifier.getType(), identifier.getValue());
            }
        }
    }

    Element entityElement() {
        Element result = new Element("intelectualEntity", CZIDLO_NS);
        EntityType entityType = entity.getEntityType();
        result.addAttribute(new Attribute("type", entityType.name()));
        return result;
    }

    void appendTimestamps(Element root) {
        appendTimestamps(root, entity, "intelectual entity");
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

    void appendPrimaryOriginator(Element root) {
        if (originator != null) {
            Element originatorEl = appendElement(root, "primaryOriginator");
            Attribute type = new Attribute("type", originator.getType().name());
            originatorEl.addAttribute(type);
            originatorEl.appendChild(originator.getValue());
        } else {
            // logger.log(Level.WARNING, "empty value of \"originator\" for entity {0}", entity.getId());
        }
    }

    void appendOtherOriginator(Element root) {
        appendElementWithContentIfNotNull(root, entity.getOtherOriginator(), "otherOriginator");
    }

    void appendPublication(Element root) {
        if (publication != null) {
            Element pubEl = appendElement(root, "publication");
            appendElementWithContentIfNotNull(pubEl, publication.getPublisher(), "publisher");
            appendElementWithContentIfNotNull(pubEl, publication.getPlace(), "place");
            appendElementWithContentIfNotNull(pubEl, publication.getYear(), "year");
        }
    }

    void appendSourceDocument(Element root) {
        if (srcDoc != null) {
            Element srcDocEl = appendElement(root, "sourceDocument");
            Element titleInfo = appendElement(srcDocEl, "titleInfo");
            appendElementWithContentIfNotNull(titleInfo, srcDoc.getTitle(), "title");
            appendElementWithContentIfNotNull(titleInfo, srcDoc.getVolumeTitle(), "volumeTitle");
            appendElementWithContentIfNotNull(titleInfo, srcDoc.getIssueTitle(), "issueTitle");
            appendElementWithContentIfNotNull(srcDocEl, srcDoc.getCcnb(), "ccnb");
            appendElementWithContentIfNotNull(srcDocEl, srcDoc.getIsbn(), "isbn");
            appendElementWithContentIfNotNull(srcDocEl, srcDoc.getIssn(), "issn");
            appendElementWithContentIfNotNull(srcDocEl, srcDoc.getOtherId(), "otherId");
            Element publicationEl = appendElement(srcDocEl, "publication");
            appendElementWithContentIfNotNull(publicationEl, srcDoc.getPublisher(), "publisher");
            appendElementWithContentIfNotNull(publicationEl, srcDoc.getPublicationPlace(), "place");
            appendElementWithContentIfNotNull(publicationEl, srcDoc.getPublicationYear(), "year");
        } else {
            logger.log(Level.WARNING, "empty value of \"source document\" for entity {0}", entity.getId());
        }
    }

    void appendAgreeAwardingInstitution(Element root) {
        Element el = appendElementWithContentIfNotNull(root, entity.getDegreeAwardingInstitution(), "degreeAwardingInstitution");
    }

    void appendEntityIdentifier(Element root, IntEntIdType type, String elementName, boolean mandatory) {
        if (identifiers != null) {
            String value = intEntIdMap.get(type);
            if (value != null) {
                Element idElement = appendElement(root, elementName);
                idElement.appendChild(value);
            } else if (mandatory) {
                logger.log(Level.WARNING, "empty value of mandatory identifier {0} for entity {1}", new Object[] { type.toString(), entity.getId() });
            }
        }
    }
}

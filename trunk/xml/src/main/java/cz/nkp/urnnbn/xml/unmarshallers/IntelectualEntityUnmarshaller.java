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

import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.OriginType;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import java.util.List;
import java.util.logging.Level;
import nu.xom.Element;
import nu.xom.Elements;

/**
 *
 * @author Martin Řehánek
 */
public abstract class IntelectualEntityUnmarshaller extends Unmarshaller {

    final Element entityEl;
    private final EntityType entityType;

    static IntelectualEntityUnmarshaller instanceOf(Element entityEl) {
        EntityType type = intelectualEntityType(entityEl);
        switch (type) {
            case MONOGRAPH:
                return new MonographUnmarshaller(entityEl);
            case MONOGRAPH_VOLUME:
                return new MonographVolumeUnmarshaller(entityEl);
            case PERIODICAL:
                return new PeriodicalUnmarshaller(entityEl);
            case PERIODICAL_VOLUME:
                return new PeriodicalVolumeUnmarshaller(entityEl);
            case PERIODICAL_ISSUE:
                return new PeriodicalIssueUnmarshaller(entityEl);
            case ANALYTICAL:
                return new AnalytickaUnmarshaller(entityEl);
            case THESIS:
                return new ThesisUnmarshaller(entityEl);
            case OTHER:
                return new OtherEntityUnmarshaller(entityEl);
            default:
                throw new RuntimeException();
        }
    }

    public IntelectualEntityUnmarshaller(Element entityEl) {
        this.entityEl = entityEl;
        this.entityType = intelectualEntityType(entityEl);
    }

    /**
     *
     * @return IntelectualEntity object, never null
     */
    IntelectualEntity getIntelectualEntity() {
        IntelectualEntity result = new IntelectualEntity();
        result.setEntityType(entityType);
        result.setDocumentType(elementContentOrNull("documentType", entityEl));
        String digitalBornStr = elementContentOrNull("digitalBorn", entityEl);
        result.setDigitalBorn(digitalBornStr == null ? false : Boolean.valueOf(digitalBornStr));
        result.setOtherOriginator(elementContentOrNull("otherOriginator", entityEl));
        result.setDegreeAwardingInstitution(elementContentOrNull("degreeAwardingInstitution", entityEl));
        return result;
    }

    private static EntityType intelectualEntityType(Element entityEl) {
        String localName = entityEl.getLocalName();
        for (EntityType type : EntityType.values()) {
            if (localName.equals(type.toString())) {
                return type;
            }
        }
        throw new RuntimeException("unkown intelectual entity " + entityEl.getLocalName());
    }

    /**
     *
     * @return Publication object or null
     */
    Publication getPublication() {
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
    Originator getOriginator() {
        Element originator = (Element) selectSingleElementOrNull("primaryOriginator", entityEl);
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
    SourceDocument getSourceDocument() {
        Element sourceDoc = (Element) selectSingleElementOrNull("sourceDocument", entityEl);
        if (sourceDoc != null) {
            SourceDocument result = new SourceDocument();
            Element titleInfo = selectSingleElementOrNull("titleInfo", sourceDoc);
            if (titleInfo != null) {
                result.setTitle(elementContentOrNull("title", titleInfo));
                result.setVolumeTitle(elementContentOrNull("volumeTitle", titleInfo));
                result.setIssueTitle(elementContentOrNull("issueTitle", titleInfo));
            }
            result.setCcnb(elementContentOrNull("ccnb", sourceDoc));
            result.setIsbn(elementContentOrNull("isbn", sourceDoc));
            result.setIssn(elementContentOrNull("issn", sourceDoc));
            result.setOtherId(elementContentOrNull("otherId", sourceDoc));
            Element publicationEl = selectSingleElementOrNull("publication", sourceDoc);
            if (publicationEl != null) {
                result.setPublisher(elementContentOrNull("publisher", publicationEl));
                result.setPublicationPlace(elementContentOrNull("place", publicationEl));
                String yearStr = elementContentOrNull("year", publicationEl);
                if (yearStr != null) {
                    result.setPublicationYear(Integer.valueOf(yearStr));
                }
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
    public abstract List<IntEntIdentifier> getIntEntIdentifiers();

    void appendId(List<IntEntIdentifier> result, IntEntIdentifier id) {
        if (id != null) {
            result.add(id);
        }
    }

    /**
     *
     * @param rootEl
     * @param elementName
     * @param type
     * @param mandatory
     * @return IntEntIdentier or null
     */
    IntEntIdentifier identifierByElementName(Element rootEl, String elementName, IntEntIdType type, boolean mandatory) {
        Elements idElements = rootEl.getChildElements(elementName, NAMESPACE_URI);
        int found = idElements.size();
        if (found == 1) {
            Element idEl = idElements.get(0);
            IntEntIdentifier result = new IntEntIdentifier();
            result.setType(type);
            result.setValue(idEl.getValue());
            return result;
        } else if (found > 1) {
            logger.log(Level.WARNING, "multiple elements {0} found, using first one", elementName);
            return null;
        } else if (found == 0 && mandatory) {
            logger.log(Level.WARNING, "mandatory element {0} not found", elementName);
            return null;
        } else {
            return null;
        }

    }
}

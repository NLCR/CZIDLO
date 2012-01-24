/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import java.util.List;
import nu.xom.Attribute;
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
public class IntelectualEntityBuilder extends XmlBuilder {

    private final IntelectualEntity entity;
    private final List<IntEntIdentifier> identifiers;
    private final Publication publication;
    private final Originator originator;
    private final SourceDocument srcDoc;

    public IntelectualEntityBuilder(IntelectualEntity entity, List<IntEntIdentifier> identifiers, Publication publication, Originator originator, SourceDocument srcDoc) {
        this.entity = entity;
        this.identifiers = identifiers;
        this.publication = publication;
        this.originator = originator;
        this.srcDoc = srcDoc;
    }

    @Override
    Element buildRootElement() {
        Element root = new Element("intelectualEntity", RESOLVER);
        Attribute entityType = new Attribute("type", entity.getEntityType().name());
        root.addAttribute(entityType);
        appendIdentifierElement(root, "INTERNAL", entity.getId());
        if (identifiers != null) {
            for (IntEntIdentifier identifier : identifiers) {
                Element idElement = addElement(root, identifier.getType().toString());
                idElement.appendChild(identifier.getValue());
            }
        }
        appendElementWithContentIfNotNull(root, entity.getTitle(), "title");
        appendElementWithContentIfNotNull(root, entity.getAlternativeTitle(), "subTitle");
        appendElementWithContentIfNotNull(root, entity.getDocumentType(), "documentType");
        appendElementWithContentIfNotNull(root, entity.getCreated(), "created");
        appendElementWithContentIfNotNull(root, entity.getLastUpdated(), "lastUpdated");
        appendElementWithContentIfNotNull(root, entity.isDigitalBorn(), "digitalBorn");
        appendElementWithContentIfNotNull(root, entity.getDegreeAwardingInstitution(), "degreeAwardingInstitution");
        if (publication != null) {
            Element pubEl = addElement(root, "publication");
            appendElementWithContentIfNotNull(pubEl, publication.getPublisher(), "publisher");
            appendElementWithContentIfNotNull(pubEl, publication.getPlace(), "place");
            appendElementWithContentIfNotNull(pubEl, publication.getYear(), "year");
        }
        if (originator != null) {
            Element originatorEl = addElement(root, "originator");
            Attribute type = new Attribute("type", originator.getType().name());
            originatorEl.addAttribute(type);
            originatorEl.appendChild(originator.getValue());
        }
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
        }
        return root;
    }
}

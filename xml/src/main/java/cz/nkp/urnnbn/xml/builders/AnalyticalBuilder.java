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
import nu.xom.Element;

/**
 *
 * @author Martin Řehánek
 */
class AnalyticalBuilder extends IntelectualEntityBuilder {

    public AnalyticalBuilder(IntelectualEntity entity, List<IntEntIdentifier> identifiers, Publication publication, Originator originator, SourceDocument srcDoc) {
        super(entity, identifiers, publication, originator, srcDoc);
    }

    @Override
    public Element buildRootElement() {
        Element root = entityElement();
        appendCreatedAndUpdated(root);
        appendTitleAndSubtitle(root);
        //appendIdentifierElement(root, "INTERNAL", entity.getId());
        appendDocumentType(root);
        appendDigitalBorn(root);
        appendOriginator(root);
        appendSourceDocument(root);
        return root;
    }
}

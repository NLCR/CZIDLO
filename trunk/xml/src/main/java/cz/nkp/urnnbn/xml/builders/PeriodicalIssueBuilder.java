/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.xml.builders;

import cz.nkp.urnnbn.core.IntEntIdType;
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
class PeriodicalIssueBuilder extends IntelectualEntityBuilder {

    public PeriodicalIssueBuilder(IntelectualEntity entity, List<IntEntIdentifier> identifiers, Publication publication, Originator originator, SourceDocument srcDoc) {
        super(entity, identifiers, publication, originator, srcDoc);
    }

    @Override
    public Element buildRootElement() {
        Element root = entityElement();
        Element titleInfo = appendElement(root, "titleInfo");
        appendEntityIdentifier(titleInfo, IntEntIdType.TITLE, "periodicalTitle", true);
        appendEntityIdentifier(titleInfo, IntEntIdType.VOLUME_TITLE, "volumeTitle", true);
        appendEntityIdentifier(titleInfo, IntEntIdType.ISSUE_TITLE, "issueTitle", true);
        appendEntityIdentifier(root, IntEntIdType.CCNB, "ccnb", false);
        appendEntityIdentifier(root, IntEntIdType.ISSN, "issn", false);
        appendEntityIdentifier(root, IntEntIdType.OTHER, "otherId", false);
        appendDocumentType(root);
        appendDigitalBorn(root);
        appendPrimaryOriginator(root);
        appendOtherOriginator(root);
        appendPublication(root);
        return root;
    }
}

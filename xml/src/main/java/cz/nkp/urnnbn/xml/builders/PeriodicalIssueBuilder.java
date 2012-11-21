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
        appendTimestamps(root);
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

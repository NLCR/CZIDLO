package cz.nkp.urnnbn.xml.apiv3.builders;

import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.*;
import nu.xom.Element;

import java.util.List;

public class SoundCollectionBuilder extends IntelectualEntityBuilder {

    public SoundCollectionBuilder(IntelectualEntity entity, List<IntEntIdentifier> identifiers, Publication publication, Originator originator, SourceDocument srcDoc) {
        super(entity, identifiers, publication, originator, srcDoc);
    }

    @Override
    public Element buildRootElement() {
        Element root = entityElement();
        appendTimestamps(root);
        Element titleInfo = appendElement(root, "titleInfo");
        appendEntityIdentifier(titleInfo, IntEntIdType.TITLE, "title", true);
        appendEntityIdentifier(titleInfo, IntEntIdType.SUB_TITLE, "subTitle", false);
        appendEntityIdentifier(root, IntEntIdType.CCNB, "ccnb", false);
        appendEntityIdentifier(root, IntEntIdType.OTHER, "otherId", false);
        appendDocumentType(root);
        appendDigitalBorn(root);
        appendPrimaryOriginator(root);
        appendOtherOriginator(root);
        appendPublication(root);
        return root;
    }
}

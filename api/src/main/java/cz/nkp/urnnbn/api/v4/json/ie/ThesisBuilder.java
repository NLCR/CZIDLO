package cz.nkp.urnnbn.api.v4.json.ie;

import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.SourceDocument;

public class ThesisBuilder extends IntelectualEntityBuilderJson {

    public ThesisBuilder(IntelectualEntity entity, List<IntEntIdentifier> identifiers, Publication publication, Originator originator,
            SourceDocument srcDoc) {
        super(entity, identifiers, publication, originator, srcDoc);
    }

    @Override
    protected String getName() {
        return "thesis";
    }

    @Override
    protected Object build() {
        try {
            JSONObject root = new JSONObject();
            appendTimestamps(root);
            JSONObject titleInfo = appendElement(root, "titleInfo");
            appendEntityIdentifier(titleInfo, IntEntIdType.TITLE, "title");
            appendEntityIdentifier(titleInfo, IntEntIdType.SUB_TITLE, "subTitle");
            appendEntityIdentifier(root, IntEntIdType.CCNB, "ccnb");
            appendEntityIdentifier(root, IntEntIdType.OTHER, "otherId");
            appendDocumentType(root);
            appendDigitalBorn(root);
            appendPrimaryOriginator(root);
            appendOtherOriginator(root);
            appendPublication(root);
            appendAgreeAwardingInstitution(root);
            return root;
        } catch (JSONException e) {
            LOGGER.severe(e.getMessage());
            return EMPTY_OBJECT;
        }
    }
}
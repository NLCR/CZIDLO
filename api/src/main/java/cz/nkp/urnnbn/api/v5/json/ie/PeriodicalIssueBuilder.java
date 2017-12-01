package cz.nkp.urnnbn.api.v5.json.ie;

import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.*;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.List;

public class PeriodicalIssueBuilder extends IntelectualEntityBuilderJson {

    public PeriodicalIssueBuilder(IntelectualEntity entity, List<IntEntIdentifier> identifiers, Publication publication, Originator originator,
            SourceDocument srcDoc) {
        super(entity, identifiers, publication, originator, srcDoc);
    }

    @Override
    protected String getName() {
        return "periodicalIssue";
    }

    @Override
    protected Object build() {
        try {
            JSONObject root = new JSONObject();
            appendTimestamps(root);
            JSONObject titleInfo = appendElement(root, "titleInfo");
            appendEntityIdentifier(titleInfo, IntEntIdType.TITLE, "periodicalTitle");
            appendEntityIdentifier(titleInfo, IntEntIdType.VOLUME_TITLE, "volumeTitle");
            appendEntityIdentifier(titleInfo, IntEntIdType.ISSUE_TITLE, "issueTitle");
            appendEntityIdentifier(root, IntEntIdType.CCNB, "ccnb");
            appendEntityIdentifier(root, IntEntIdType.ISSN, "issn");
            appendEntityIdentifier(root, IntEntIdType.OTHER, "otherId");
            appendDocumentType(root);
            appendDigitalBorn(root);
            appendPrimaryOriginator(root);
            appendOtherOriginator(root);
            appendPublication(root);
            return root;
        } catch (JSONException e) {
            LOGGER.severe(e.getMessage());
            return EMPTY_OBJECT;
        }
    }
}

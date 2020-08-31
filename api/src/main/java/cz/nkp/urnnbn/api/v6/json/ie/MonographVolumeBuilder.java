package cz.nkp.urnnbn.api.v6.json.ie;

import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.*;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.List;

public class MonographVolumeBuilder extends IntelectualEntityBuilderJson {

    public MonographVolumeBuilder(IntelectualEntity entity, List<IntEntIdentifier> identifiers, Publication publication, Originator originator,
            SourceDocument srcDoc) {
        super(entity, identifiers, publication, originator, srcDoc);
    }

    @Override
    protected String getName() {
        return "monographVolume";
    }

    @Override
    protected Object build() {
        try {
            JSONObject root = new JSONObject();
            appendTimestamps(root);
            appendTimestamps(root);
            JSONObject titleInfo = appendElement(root, "titleInfo");
            appendEntityIdentifier(titleInfo, IntEntIdType.TITLE, "monographTitle");
            appendEntityIdentifier(titleInfo, IntEntIdType.VOLUME_TITLE, "volumeTitle");
            appendEntityIdentifier(root, IntEntIdType.CCNB, "ccnb");
            appendEntityIdentifier(root, IntEntIdType.ISBN, "isbn");
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

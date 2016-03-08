package cz.nkp.urnnbn.api.v4.json.ie;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import cz.nkp.urnnbn.api.v4.json.JsonBuilder;
import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.SourceDocument;

public abstract class IntelectualEntityBuilderJson extends JsonBuilder {

    @Override
    protected String getName() {
        return "todo:iebuilder";
    }

    public static IntelectualEntityBuilderJson instanceOf(IntelectualEntity entity, List<IntEntIdentifier> ieIdentfiers, Publication pub,
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

    public IntelectualEntityBuilderJson(IntelectualEntity entity, List<IntEntIdentifier> identifiers, Publication publication, Originator originator,
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

    void appendTimestamps(JSONObject root) throws JSONException {
        appendTimestamps(root, entity);
    }

    void appendDocumentType(JSONObject root) throws JSONException {
        root.put("documentType", entity.getDocumentType());
    }

    void appendDigitalBorn(JSONObject root) throws JSONException {
        appendElementWithContentIfNotNull(root, entity.isDigitalBorn(), "digitalBorn");
    }

    void appendPrimaryOriginator(JSONObject root) throws JSONException {
        if (originator != null) {
            JSONObject originatorEl = appendElement(root, "primaryOriginator");
            originatorEl.put("type", originator.getType().name());
            originatorEl.put("value", originator.getValue());
        }
    }

    void appendOtherOriginator(JSONObject root) throws JSONException {
        if (entity.getOtherOriginator() != null) {
            root.put("otherOriginator", entity.getOtherOriginator());
        }
    }

    void appendPublication(JSONObject root) throws JSONException {
        if (publication != null) {
            JSONObject pubEl = appendElement(root, "publication");
            appendElementWithContentIfNotNull(pubEl, publication.getPublisher(), "publisher");
            appendElementWithContentIfNotNull(pubEl, publication.getPlace(), "place");
            appendElementWithContentIfNotNull(pubEl, publication.getYear(), "year");
        }
    }

    void appendSourceDocument(JSONObject root) throws JSONException {
        if (srcDoc != null) {
            JSONObject srcDocEl = appendElement(root, "sourceDocument");
            JSONObject titleInfo = appendElement(srcDocEl, "titleInfo");
            appendElementWithContentIfNotNull(titleInfo, srcDoc.getTitle(), "title");
            appendElementWithContentIfNotNull(titleInfo, srcDoc.getVolumeTitle(), "volumeTitle");
            appendElementWithContentIfNotNull(titleInfo, srcDoc.getIssueTitle(), "issueTitle");
            appendElementWithContentIfNotNull(srcDocEl, srcDoc.getCcnb(), "ccnb");
            appendElementWithContentIfNotNull(srcDocEl, srcDoc.getIsbn(), "isbn");
            appendElementWithContentIfNotNull(srcDocEl, srcDoc.getIssn(), "issn");
            appendElementWithContentIfNotNull(srcDocEl, srcDoc.getOtherId(), "otherId");
            JSONObject publicationEl = appendElement(srcDocEl, "publication");
            appendElementWithContentIfNotNull(publicationEl, srcDoc.getPublisher(), "publisher");
            appendElementWithContentIfNotNull(publicationEl, srcDoc.getPublicationPlace(), "place");
            appendElementWithContentIfNotNull(publicationEl, srcDoc.getPublicationYear(), "year");
        }
    }

    void appendAgreeAwardingInstitution(JSONObject root) throws JSONException {
        appendElementWithContentIfNotNull(root, entity.getDegreeAwardingInstitution(), "degreeAwardingInstitution");
    }

    void appendEntityIdentifier(JSONObject root, IntEntIdType type, String elementName) throws JSONException {
        if (identifiers != null) {
            String value = intEntIdMap.get(type);
            if (value != null) {
                root.put(elementName, value);
            }
        }
    }

}

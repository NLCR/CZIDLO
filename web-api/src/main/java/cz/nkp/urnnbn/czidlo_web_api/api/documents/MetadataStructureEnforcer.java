package cz.nkp.urnnbn.czidlo_web_api.api.documents;

import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.services.DigDocRegistrationData;

public class MetadataStructureEnforcer {

    private final DigDocRegistrationData data;

    public MetadataStructureEnforcer(DigDocRegistrationData data) {
        this.data = data;
    }

    public static class MetadataStructureException extends Exception {

        public MetadataStructureException(EntityType entityType, String message) {
            super(entityType + ": " + message);
        }

        public MetadataStructureException(String message) {
            super(message);
        }
    }

    public void run() throws MetadataStructureException {
        if (data.getEntity() == null) {
            throw new MetadataStructureException("Missing intellectual entity metadata.");
        }
        if (data.getDigitalDocument() == null) {
            throw new MetadataStructureException("Missing digital document metadata.");
        }
        if (data.getRegistrarCode() == null) {
            throw new MetadataStructureException("Missing registrar code.");
        }
        //IDENTIFIERS
        switch (data.getEntity().getEntityType()) {
            case MONOGRAPH -> {
                mustHaveId(IntEntIdType.TITLE);
                cantHaveId(IntEntIdType.VOLUME_TITLE);
                cantHaveId(IntEntIdType.ISSUE_TITLE);
                cantHaveId(IntEntIdType.ISSN);
            }
            case MONOGRAPH_VOLUME -> {
                mustHaveId(IntEntIdType.TITLE);
                mustHaveId(IntEntIdType.VOLUME_TITLE);
                cantHaveId(IntEntIdType.ISSUE_TITLE);
                cantHaveId(IntEntIdType.ISBN);
                cantHaveId(IntEntIdType.SUB_TITLE);
            }
            case PERIODICAL -> {
                mustHaveId(IntEntIdType.TITLE);
                cantHaveId(IntEntIdType.VOLUME_TITLE);
                cantHaveId(IntEntIdType.ISSUE_TITLE);
                cantHaveId(IntEntIdType.ISBN);
            }
            case PERIODICAL_VOLUME -> {
                mustHaveId(IntEntIdType.TITLE);
                mustHaveId(IntEntIdType.VOLUME_TITLE);
                cantHaveId(IntEntIdType.SUB_TITLE);
                cantHaveId(IntEntIdType.ISSUE_TITLE);
                cantHaveId(IntEntIdType.ISBN);
            }
            case PERIODICAL_ISSUE -> {
                mustHaveId(IntEntIdType.TITLE);
                mustHaveId(IntEntIdType.VOLUME_TITLE);
                mustHaveId(IntEntIdType.ISSUE_TITLE);
                cantHaveId(IntEntIdType.SUB_TITLE);
                cantHaveId(IntEntIdType.ISBN);
            }
            case ANALYTICAL -> {
                mustHaveId(IntEntIdType.TITLE);
                cantHaveId(IntEntIdType.VOLUME_TITLE);
                cantHaveId(IntEntIdType.ISSUE_TITLE);
                cantHaveId(IntEntIdType.ISBN);
                cantHaveId(IntEntIdType.ISSN);
                cantHaveId(IntEntIdType.CCNB);
            }
            case THESIS -> {
                mustHaveId(IntEntIdType.TITLE);
                cantHaveId(IntEntIdType.VOLUME_TITLE);
                cantHaveId(IntEntIdType.ISSUE_TITLE);
                cantHaveId(IntEntIdType.ISBN);
                cantHaveId(IntEntIdType.ISSN);
            }
            case OTHER -> {
                mustHaveId(IntEntIdType.TITLE);
                cantHaveId(IntEntIdType.VOLUME_TITLE);
                cantHaveId(IntEntIdType.ISSUE_TITLE);
                cantHaveId(IntEntIdType.ISSN);
            }
            case SOUND_COLLECTION -> {
                mustHaveId(IntEntIdType.TITLE);
                cantHaveId(IntEntIdType.VOLUME_TITLE);
                cantHaveId(IntEntIdType.ISSUE_TITLE);
                cantHaveId(IntEntIdType.ISSN);
                cantHaveId(IntEntIdType.ISBN);
            }
        }
    }

    /**
     * check that data contains identifier of given type and is not null or empty
     */
    private boolean hasIdentifier(IntEntIdType type) {
        return data.getIntEntIds().stream()
                .anyMatch(id -> id.getType() == type && id.getValue() != null && !id.getValue().isEmpty());
    }

    private void mustHaveId(IntEntIdType type) throws MetadataStructureException {
        if (!hasIdentifier(type)) {
            throw new MetadataStructureException(getEntityType(), "Missing required identifier of type: " + type);
        }
    }

    private void cantHaveId(IntEntIdType type) throws MetadataStructureException {
        if (hasIdentifier(type)) {
            throw new MetadataStructureException(getEntityType(), "Identifier of type " + type + " is not allowed.");
        }
    }

    private EntityType getEntityType() {
        return data.getEntity().getEntityType();
    }

}

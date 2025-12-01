package cz.nkp.urnnbn.czidlo_web_api.api.documents;

import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.services.DigDocRegistrationData;

public class MetadataStructureEnforcer {

    private final DigDocRegistrationData data;

    public MetadataStructureEnforcer(DigDocRegistrationData data) {
        this.data = data;
    }

    public static class MetadataStructureException extends Exception {

        public MetadataStructureException(EntityType entityType, String message) {
            super((entityType == null ? "" : entityType.name()) + ": " + message);
        }

        public MetadataStructureException(String message) {
            super(message);
        }
    }

    public void check() throws MetadataStructureException {
        //REGISTRAR CODE
        if (data.getRegistrarCode() == null) {
            throw new MetadataStructureException("Missing registrar code.");
        }
        //INTELLECTUAL ENTITY
        if (data.getEntity() == null) {
            throw new MetadataStructureException("Missing intellectual entity metadata.");
        }
        //IE - IDENTIFIERS
        checkIeIdentifiers();
        //PRIMARY ORIGINATOR
        Originator originator = data.getOriginator();
        if (originator == null) {
            throw new MetadataStructureException("Missing originator metadata.");
        } else {
            if (originator.getValue() == null || originator.getValue().isEmpty()) {
                throw new MetadataStructureException("Originator value is required.");
            }
            if (originator.getType() == null) {
                throw new MetadataStructureException("Originator type is required.");
            }
        }
        //SOURCE DOCUMENT
        if (getEntityType() == EntityType.ANALYTICAL) {
            checkSourceDocumentForAnalytical();
        } else {
            if (data.getSourceDoc() != null) {
                throw new MetadataStructureException(getEntityType(), "Source document metadata is not allowed for this entity type.");
            }
        }
        //DIGITAL DOCUMENT
        if (data.getDigitalDocument() == null) {
            throw new MetadataStructureException("Missing digital document metadata.");
        }
        checkDigitalDocument();
    }

    private void checkDigitalDocument() throws MetadataStructureException {
        DigitalDocument digDoc = data.getDigitalDocument();
        if (digDoc.getFormat() == null || digDoc.getFormat().isEmpty()) {
            throw new MetadataStructureException("Digital document format is required.");
        }
        if (digDoc.getResolutionHorizontal() == null) {
            throw new MetadataStructureException("Digital document horizontal resolution is required.");
        }
        if (digDoc.getResolutionVertical() == null) {
            throw new MetadataStructureException("Digital document vertical resolution is required.");
        }
        if (digDoc.getCompression() == null || digDoc.getCompression().isEmpty()) {
            throw new MetadataStructureException("Digital document compression is required.");
        }
        if (digDoc.getPictureWidth() == null) {
            throw new MetadataStructureException("Digital document picture width is required.");
        }
        if (digDoc.getPictureHeight() == null) {
            throw new MetadataStructureException("Digital document picture height is required.");
        }
    }

    private void checkSourceDocumentForAnalytical() throws MetadataStructureException {
        SourceDocument srcDoc = data.getSourceDoc();
        if (srcDoc == null) {
            throw new MetadataStructureException(EntityType.ANALYTICAL, "Missing source document metadata for this entity type.");
        }
        if (srcDoc.getTitle() == null || srcDoc.getTitle().isEmpty()) {
            throw new MetadataStructureException(EntityType.ANALYTICAL, "Source document title is required for this entity type.");
        }
    }

    private void checkIeIdentifiers() throws MetadataStructureException {
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
            throw new MetadataStructureException(getEntityType(), "Missing required identifier of type: " + type.name());
        }
    }

    private void cantHaveId(IntEntIdType type) throws MetadataStructureException {
        if (hasIdentifier(type)) {
            throw new MetadataStructureException(getEntityType(), "Identifier of type " + type.name() + " is not allowed.");
        }
    }

    private EntityType getEntityType() {
        return data.getEntity().getEntityType();
    }

}

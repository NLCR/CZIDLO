package cz.nkp.urnnbn.czidlo_web_api.api.documents.core;

import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.dto.*;
import cz.nkp.urnnbn.czidlo_web_api.api.Utils;

import java.util.Date;
import java.util.List;

public class IntEnt {

    public Long id;
    public EntityType entityType;
    public Date created;
    public Date modified;
    public String documentType;
    public Boolean digitalBorn;
    public String otherOriginator;
    public String degreeAwardingInstitution;

    public Orig originator;
    public Publ publication;
    public SrcDoc sourceDocument;
    public List<IeId> ieIdentifiers;

    public static IntEnt from(IntelectualEntity dtoIe, Orig originator, Publ publication, SrcDoc srcDoc, List<IeId> ieIds) {
        if (dtoIe == null) {
            return null;
        }
        IntEnt result = new IntEnt();
        result.id = dtoIe.getId();
        result.entityType = dtoIe.getEntityType();
        result.created = Utils.dateTimeToDate(dtoIe.getCreated());
        result.modified = Utils.dateTimeToDate(dtoIe.getModified());
        result.documentType = dtoIe.getDocumentType();
        result.digitalBorn = dtoIe.isDigitalBorn();
        result.otherOriginator = dtoIe.getOtherOriginator();
        result.degreeAwardingInstitution = dtoIe.getDegreeAwardingInstitution();
        result.originator = originator;
        result.publication = publication;
        result.sourceDocument = srcDoc;
        result.ieIdentifiers = ieIds;
        return result;
    }

    public Long getId() {
        return id;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Date getCreated() {
        return created;
    }

    public Date getModified() {
        return modified;
    }

    public String getDocumentType() {
        return documentType;
    }

    public Boolean getDigitalBorn() {
        return digitalBorn;
    }

    public String getOtherOriginator() {
        return otherOriginator;
    }

    public String getDegreeAwardingInstitution() {
        return degreeAwardingInstitution;
    }

    public Orig getOriginator() {
        return originator;
    }

    public Publ getPublication() {
        return publication;
    }

    public SrcDoc getSourceDocument() {
        return sourceDocument;
    }

    public List<IeId> getIeIdentifiers() {
        return ieIdentifiers;
    }

    public IntelectualEntity toDtoIntEnt() {
        IntelectualEntity result = new IntelectualEntity();
        result.setEntityType(this.entityType);
        result.setDocumentType(this.documentType);
        if (this.digitalBorn != null) {
            result.setDigitalBorn(this.digitalBorn);
        }
        result.setOtherOriginator(this.otherOriginator);
        result.setDegreeAwardingInstitution(degreeAwardingInstitution);
        return result;
    }

    public List<IntEntIdentifier> toDtoIeIds() {
        if (ieIdentifiers == null) {
            return List.of();
        }
        List<IntEntIdentifier> result = new java.util.ArrayList<>();
        for (IeId ieId : ieIdentifiers) {
            result.add(ieId.toDto());
        }
        return result;
    }

    public Originator toDtoOriginator() {
        if (originator == null) {
            return null;
        }
        Originator dto = new Originator();
        dto.setType(originator.type);
        dto.setValue(originator.value);
        return dto;
    }

    public Publication toDtoPublication() {
        if (publication == null) {
            return null;
        }
        Publication dto = new Publication();
        dto.setPublisher(publication.publisher);
        dto.setPlace(publication.place);
        dto.setYear(publication.year);
        return dto;
    }

    public SourceDocument toDtoSrcDoc() {
        if (sourceDocument == null) {
            return null;
        }
        SourceDocument dto = new SourceDocument();
        dto.setCcnb(sourceDocument.ccnb);
        dto.setIsbn(sourceDocument.isbn);
        dto.setIssn(sourceDocument.issn);
        dto.setOtherId(sourceDocument.otherId);
        dto.setTitle(sourceDocument.title);
        dto.setVolumeTitle(sourceDocument.volumeTitle);
        dto.setIssueTitle(sourceDocument.issueTitle);
        dto.setPublicationPlace(sourceDocument.publicationPlace);
        dto.setPublisher(sourceDocument.publisher);
        dto.setPublicationYear(sourceDocument.publicationYear);
        return dto;
    }

    @Override
    public String toString() {
        return "IntEnt{" +
                "id=" + id +
                ",\n entityType=" + entityType +
                ",\n created=" + created +
                ",\n modified=" + modified +
                ",\n documentType='" + documentType + '\'' +
                ",\n digitalBorn=" + digitalBorn +
                ",\n otherOriginator='" + otherOriginator + '\'' +
                ",\n degreeAwardingInstitution='" + degreeAwardingInstitution + '\'' +
                ",\n originator=" + originator +
                ",\n publication=" + publication +
                ",\n sourceDocument=" + sourceDocument +
                ",\n ieIdentifiers=" + ieIdentifiers +
                "\n}";
    }
}

package cz.nkp.urnnbn.czidlo_web_api.api.documents.core;

import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.czidlo_web_api.api.Utils;

import java.util.Date;
import java.util.List;

public class Entity {

    private Long id;
    private EntityType entityType;
    private Date created;
    private Date modified;
    private String documentType;
    private Boolean digitalBorn;
    private String otherOriginator;
    private String degreeAwardingInstitution;

    private Orig originator;
    private Publ publication;
    private SrcDoc sourceDocument;
    private List<IeId> ieIdentifiers;

    public static Entity from(IntelectualEntity dtoIe, Orig originator, Publ publication, SrcDoc srcDoc, List<IeId> ieIds) {
        if (dtoIe == null) {
            return null;
        }
        Entity result = new Entity();
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
}

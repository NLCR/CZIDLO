package cz.nkp.urnnbn.czidlo_web_api.api.documents.core;

import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.czidlo_web_api.api.Utils;
import org.joda.time.DateTime;

import java.util.Date;

public class Entity {

    private Long id;
    private EntityType entityType;
    private Date created;
    private Date modified;
    private String documentType;
    private Boolean digitalBorn;
    private String otherOriginator;
    private String degreeAwardingInstitution;

    //TODO: originator
    //TODO: publication
    //TODO: source document
    //TODO: ieidentifiers

    public static Entity from(IntelectualEntity ie) {
        if (ie == null) {
            return null;
        }
        Entity entity = new Entity();
        entity.id = ie.getId();
        entity.entityType = ie.getEntityType();
        entity.created = Utils.dateTimeToDate(ie.getCreated());
        entity.modified = Utils.dateTimeToDate(ie.getModified());
        entity.documentType = ie.getDocumentType();
        entity.digitalBorn = ie.isDigitalBorn();
        entity.otherOriginator = ie.getOtherOriginator();
        entity.degreeAwardingInstitution = ie.getDegreeAwardingInstitution();
        return entity;
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
}

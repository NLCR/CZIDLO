/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

import cz.nkp.urnnbn.core.EntityType;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public class IntelectualEntity implements IdentifiableWithDatestamps {

    private Long id;
    private EntityType entityType;
    private DateTime created;
    private DateTime modified;
    private String documentType;
    private Boolean digitalBorn = false;
    private String otherOriginator;
    private String degreeAwardingInstitution;

    public IntelectualEntity() {
    }

    public IntelectualEntity(IntelectualEntity original) {
        this.id = original.getId();
        this.entityType = original.getEntityType();
        this.created = original.getCreated();
        this.modified = original.getModified();
        this.documentType = original.getDocumentType();
        this.digitalBorn = original.isDigitalBorn();
        this.otherOriginator = original.getOtherOriginator();
        this.degreeAwardingInstitution = original.getDegreeAwardingInstitution();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    public String getDegreeAwardingInstitution() {
        return degreeAwardingInstitution;
    }

    public void setDegreeAwardingInstitution(String degreeAwardingInstitution) {
        this.degreeAwardingInstitution = degreeAwardingInstitution;
    }

    public Boolean isDigitalBorn() {
        return digitalBorn;
    }

    public void setDigitalBorn(Boolean digitalBorn) {
        this.digitalBorn = digitalBorn;
    }

    public String getOtherOriginator() {
        return otherOriginator;
    }

    public void setOtherOriginator(String otherOriginator) {
        this.otherOriginator = otherOriginator;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    @Override
    public DateTime getModified() {
        return modified;
    }

    public void setModified(DateTime modified) {
        this.modified = modified;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IntelectualEntity other = (IntelectualEntity) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}

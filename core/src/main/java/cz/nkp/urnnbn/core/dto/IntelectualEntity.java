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

    private long id;
    private EntityType entityType;
    private DateTime created;
    private DateTime modified;
    private String documentType;
    private boolean digitalBorn = false;
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

    public boolean isDigitalBorn() {
        return digitalBorn;
    }

    public void setDigitalBorn(boolean digitalBorn) {
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
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
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
        if (this.id != other.id) {
            return false;
        }
        if (this.entityType != other.entityType) {
            return false;
        }
        if ((this.documentType == null) ? (other.documentType != null) : !this.documentType.equals(other.documentType)) {
            return false;
        }
        if (this.digitalBorn != other.digitalBorn) {
            return false;
        }
        if ((this.degreeAwardingInstitution == null) ? (other.degreeAwardingInstitution != null) : !this.degreeAwardingInstitution.equals(other.degreeAwardingInstitution)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 43 * hash + (this.entityType != null ? this.entityType.hashCode() : 0);
        hash = 43 * hash + (this.documentType != null ? this.documentType.hashCode() : 0);
        hash = 43 * hash + (this.digitalBorn ? 1 : 0);
        hash = 43 * hash + (this.degreeAwardingInstitution != null ? this.degreeAwardingInstitution.hashCode() : 0);
        return hash;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

/**
 *
 * @author Martin Řehánek
 */
public class Catalog implements IdentifiableByLongAttribute {

    private long id;
    private long registrarId;
    private String name;
    private String description;
    private String urlPrefix;

    public Catalog() {
    }

    public Catalog(Catalog inserted) {
        id = inserted.getId();
        registrarId = inserted.getRegistrarId();
        name = inserted.getName();
        description = inserted.getDescription();
        urlPrefix = inserted.getUrlPrefix();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRegistrarId() {
        return registrarId;
    }

    public void setRegistrarId(long registrarId) {
        this.registrarId = registrarId;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Catalog other = (Catalog) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.registrarId != other.registrarId) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if ((this.urlPrefix == null) ? (other.urlPrefix != null) : !this.urlPrefix.equals(other.urlPrefix)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 29 * hash + (int) (this.registrarId ^ (this.registrarId >>> 32));
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 29 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 29 * hash + (this.urlPrefix != null ? this.urlPrefix.hashCode() : 0);
        return hash;
    }
}

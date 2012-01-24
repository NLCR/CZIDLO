/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalLibrary implements IdentifiableByLongAttribute {

    private long id;
    private long registrarId;
    private String name;
    private String description;
    private String url;

    public DigitalLibrary() {
    }

    public DigitalLibrary(DigitalLibrary original) {
        id = original.getId();
        registrarId = original.getRegistrarId();
        name = original.getName();
        description = original.getDescription();
        url = original.getUrl();
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DigitalLibrary other = (DigitalLibrary) obj;
        if (this.id != other.id) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if ((this.url == null) ? (other.url != null) : !this.url.equals(other.url)) {
            return false;
        }
        if (this.registrarId != other.registrarId) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 73 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 73 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 73 * hash + (this.url != null ? this.url.hashCode() : 0);
        hash = 73 * hash + (int) (this.registrarId ^ (this.registrarId >>> 32));
        return hash;
    }
}

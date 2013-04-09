/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalLibrary implements IdentifiableWithDatestamps {

    private Long id;
    private Long registrarId;
    private DateTime created;
    private DateTime modified;
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

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getRegistrarId() {
        return registrarId;
    }

    public void setRegistrarId(Long registrarId) {
        this.registrarId = registrarId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
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
        final DigitalLibrary other = (DigitalLibrary) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("DigitalLibrary{");
        result.append("id=").append(id);
        result.append(", ");
        result.append("registrarId=").append(registrarId);
        result.append(", ");
        if (created != null) {
            result.append("created=").append(created);
            result.append(", ");
        }
        if (modified != null) {
            result.append("modified=").append(created);
            result.append(", ");
        }
        result.append("name=").append(name);
        result.append(", ");
        if (description != null) {
            result.append("description=").append(description);
            result.append(", ");
        }
        result.append("url=").append(url);
        result.append("}");
        return result.toString();
    }
}

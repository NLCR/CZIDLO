package cz.nkp.urnnbn.shared.dto;

import java.io.Serializable;

public class DigitalInstanceDTO implements Serializable {

    public enum ACCESS_RESTRICTION {
        UNKNOWN,
        UNLIMITED_ACCESS,
        LIMITED_ACCESS
    }

    private static final long serialVersionUID = -9032263437314075051L;
    private Long id;
    private DigitalLibraryDTO library;
    private String created;
    private String deactivated;
    private Boolean active;
    private String url;
    private String format;
    private String accessibility;
    private ACCESS_RESTRICTION accessRestriction;

    public DigitalInstanceDTO() {
        this.accessRestriction = ACCESS_RESTRICTION.UNKNOWN;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DigitalLibraryDTO getLibrary() {
        return library;
    }

    public void setLibrary(DigitalLibraryDTO library) {
        this.library = library;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String published) {
        this.created = published;
    }

    public String getDeactivated() {
        return deactivated;
    }

    public void setDeactivated(String modified) {
        this.deactivated = modified;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(String accessibility) {
        this.accessibility = accessibility;
    }

    public ACCESS_RESTRICTION getAccessRestriction() {
        return accessRestriction;
    }

    public void setAccessRestriction(ACCESS_RESTRICTION accessRestriction) {
        this.accessRestriction = accessRestriction;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DigitalInstanceDTO other = (DigitalInstanceDTO) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}

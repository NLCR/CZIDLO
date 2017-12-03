/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

import cz.nkp.urnnbn.core.AccessRestriction;
import org.joda.time.DateTime;

/**
 * @author Martin Řehánek
 */
public class DigitalInstance implements IdentifiableByLongAttribute {

    private Long id;
    private Long digDocId;
    private Long libraryId;
    private DateTime created;
    private DateTime deactivated;
    private Boolean active;
    private String url;
    private String format;
    private String accessibility;
    private AccessRestriction accessRestriction;

    public Long getDigDocId() {
        return digDocId;
    }

    public void setDigDocId(Long digDocId) {
        this.digDocId = digDocId;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(Long libraryId) {
        this.libraryId = libraryId;
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

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    public DateTime getDeactivated() {
        return deactivated;
    }

    public void setDeactivated(DateTime modified) {
        this.deactivated = modified;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public AccessRestriction getAccessRestriction() {
        return accessRestriction;
    }

    public void setAccessRestriction(AccessRestriction accessRestriction) {
        this.accessRestriction = accessRestriction;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DigitalInstance other = (DigitalInstance) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DigitalInstance{");
        boolean someFieldAlreadyPresent = false;
        if (id != null) {
            if (someFieldAlreadyPresent) {
                builder.append(", ");
            }
            builder.append("id='").append(id).append('\'');
            someFieldAlreadyPresent = true;
        }
        if (libraryId != null) {
            if (someFieldAlreadyPresent) {
                builder.append(", ");
            }
            builder.append("libraryId='").append(libraryId).append('\'');
            someFieldAlreadyPresent = true;
        }
        if (digDocId != null) {
            if (someFieldAlreadyPresent) {
                builder.append(", ");
            }
            builder.append("digDocId='").append(digDocId).append('\'');
            someFieldAlreadyPresent = true;
        }
        if (url != null && !url.isEmpty()) {
            if (someFieldAlreadyPresent) {
                builder.append(", ");
            }
            builder.append("url='").append(url).append('\'');
            someFieldAlreadyPresent = true;
        }
        if (format != null && !format.isEmpty()) {
            if (someFieldAlreadyPresent) {
                builder.append(", ");
            }
            builder.append("format='").append(format).append('\'');
            someFieldAlreadyPresent = true;
        }
        if (accessibility != null && !accessibility.isEmpty()) {
            if (someFieldAlreadyPresent) {
                builder.append(", ");
            }
            builder.append("accessibility='").append(accessibility).append('\'');
            someFieldAlreadyPresent = true;
        }
        if (accessRestriction != null) {
            if (someFieldAlreadyPresent) {
                builder.append(", ");
            }
            builder.append("accessRestriction='").append(accessRestriction).append('\'');
            someFieldAlreadyPresent = true;
        }
        if (active != null) {
            if (someFieldAlreadyPresent) {
                builder.append(", ");
            }
            builder.append("active='").append(active).append('\'');
            someFieldAlreadyPresent = true;
        }
        if (created != null) {
            if (someFieldAlreadyPresent) {
                builder.append(", ");
            }
            builder.append("created='").append(created).append('\'');
            someFieldAlreadyPresent = true;
        }
        if (deactivated != null) {
            if (someFieldAlreadyPresent) {
                builder.append(", ");
            }
            builder.append("deactivated='").append(deactivated).append('\'');
            someFieldAlreadyPresent = true;
        }
        builder.append('}');
        return builder.toString();
    }
}

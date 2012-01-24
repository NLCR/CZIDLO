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
public class DigitalRepresentation implements IdentifiableByLongAttribute {

    private long id;
    private long intEntId;
    private long registrarId;
    private long archiverId;
    private DateTime created;
    private DateTime lastUpdated;
    private String format;
    private String extent;
    private String resolution;
    private String colorDepth;
    private String accessibility;
    private String financedFrom;

    public DigitalRepresentation() {
    }

    public DigitalRepresentation(DigitalRepresentation original) {
        id = original.getId();
        intEntId = original.getIntEntId();
        registrarId = original.getRegistrarId();
        archiverId = original.getArchiverId();
        created = original.getCreated();
        lastUpdated = original.getLastUpdated();
        format = original.getFormat();
        extent = original.getExtent();
        resolution = original.getResolution();
        colorDepth = original.getColorDepth();
        accessibility = original.getAccessibility();
        financedFrom = original.getFinancedFrom();
    }

    public String getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(String accessibility) {
        this.accessibility = accessibility;
    }

    public long getArchiverId() {
        return archiverId;
    }

    public void setArchiverId(long archiverId) {
        this.archiverId = archiverId;
    }

    public String getColorDepth() {
        return colorDepth;
    }

    public void setColorDepth(String colorDepth) {
        this.colorDepth = colorDepth;
    }

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    public String getExtent() {
        return extent;
    }

    public void setExtent(String extent) {
        this.extent = extent;
    }

    public String getFinancedFrom() {
        return financedFrom;
    }

    public void setFinancedFrom(String financedFrom) {
        this.financedFrom = financedFrom;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIntEntId() {
        return intEntId;
    }

    public void setIntEntId(long intEntId) {
        this.intEntId = intEntId;
    }

    public DateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getRegistrarId() {
        return registrarId;
    }

    public void setRegistrarId(long registrarId) {
        this.registrarId = registrarId;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DigitalRepresentation other = (DigitalRepresentation) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.intEntId != other.intEntId) {
            return false;
        }
        if (this.registrarId != other.registrarId) {
            return false;
        }
        if (this.archiverId != other.archiverId) {
            return false;
        }
        if ((this.format == null) ? (other.format != null) : !this.format.equals(other.format)) {
            return false;
        }
        if ((this.extent == null) ? (other.extent != null) : !this.extent.equals(other.extent)) {
            return false;
        }
        if ((this.resolution == null) ? (other.resolution != null) : !this.resolution.equals(other.resolution)) {
            return false;
        }
        if ((this.colorDepth == null) ? (other.colorDepth != null) : !this.colorDepth.equals(other.colorDepth)) {
            return false;
        }
        if ((this.accessibility == null) ? (other.accessibility != null) : !this.accessibility.equals(other.accessibility)) {
            return false;
        }
        if ((this.financedFrom == null) ? (other.financedFrom != null) : !this.financedFrom.equals(other.financedFrom)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 17 * hash + (int) (this.intEntId ^ (this.intEntId >>> 32));
        hash = 17 * hash + (int) (this.registrarId ^ (this.registrarId >>> 32));
        hash = 17 * hash + (int) (this.archiverId ^ (this.archiverId >>> 32));
        hash = 17 * hash + (this.format != null ? this.format.hashCode() : 0);
        hash = 17 * hash + (this.extent != null ? this.extent.hashCode() : 0);
        hash = 17 * hash + (this.resolution != null ? this.resolution.hashCode() : 0);
        hash = 17 * hash + (this.colorDepth != null ? this.colorDepth.hashCode() : 0);
        hash = 17 * hash + (this.accessibility != null ? this.accessibility.hashCode() : 0);
        hash = 17 * hash + (this.financedFrom != null ? this.financedFrom.hashCode() : 0);
        return hash;
    }
}

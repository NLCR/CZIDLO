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
public class DigitalDocument implements IdentifiableByLongAttribute {

    private long id;
    private long intEntId;
    private long registrarId;
    private long archiverId;
    private DateTime created;
    private DateTime lastUpdated;
    private String extent;
    private String resolution;
    private String colorDepth;
    private String financedFrom;
    private String contractNumber;

    public DigitalDocument() {
    }

    public DigitalDocument(DigitalDocument original) {
        id = original.getId();
        intEntId = original.getIntEntId();
        registrarId = original.getRegistrarId();
        archiverId = original.getArchiverId();
        created = original.getCreated();
        lastUpdated = original.getLastUpdated();
        extent = original.getExtent();
        resolution = original.getResolution();
        colorDepth = original.getColorDepth();
        financedFrom = original.getFinancedFrom();
        contractNumber = original.getContractNumber();
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

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
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
        final DigitalDocument other = (DigitalDocument) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }
}

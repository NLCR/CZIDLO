package cz.nkp.urnnbn.core;

import org.joda.time.DateTime;

public class UrnNbnExport {

    private String urnNbn;

    private DateTime reserved;

    private DateTime registered;

    private DateTime deactivated;

    private String entityType;
    private boolean active;

    private boolean cnbAssigned;

    private boolean issnAssigned;

    private boolean isbnAssigned;

    private String title;

    private String subtitle;

    private String volumeTitle;

    private String issueTitle;

    private int numberOfDigitalInstances;

    public String getUrnNbn() {
        return urnNbn;
    }

    public void setUrnNbn(String urnNbn) {
        this.urnNbn = urnNbn;
    }

    public DateTime getReserved() {
        return reserved;
    }

    public void setReserved(DateTime reserved) {
        this.reserved = reserved;
    }

    public DateTime getRegistered() {
        return registered;
    }

    public void setRegistered(DateTime registered) {
        this.registered = registered;
    }

    public DateTime getDeactivated() {
        return deactivated;
    }

    public void setDeactivated(DateTime deactivated) {
        this.deactivated = deactivated;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isCnbAssigned() {
        return cnbAssigned;
    }

    public void setCnbAssigned(boolean cnbAssigned) {
        this.cnbAssigned = cnbAssigned;
    }

    public boolean isIssnAssigned() {
        return issnAssigned;
    }

    public void setIssnAssigned(boolean issnAssigned) {
        this.issnAssigned = issnAssigned;
    }

    public boolean isIsbnAssigned() {
        return isbnAssigned;
    }

    public void setIsbnAssigned(boolean isbnAssigned) {
        this.isbnAssigned = isbnAssigned;
    }

    public int getNumberOfDigitalInstances() {
        return numberOfDigitalInstances;
    }

    public void setNumberOfDigitalInstances(int numberOfDigitalInstances) {
        this.numberOfDigitalInstances = numberOfDigitalInstances;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getVolumeTitle() {
        return volumeTitle;
    }

    public void setVolumeTitle(String volumeTitle) {
        this.volumeTitle = volumeTitle;
    }

    public String getIssueTitle() {
        return issueTitle;
    }

    public void setIssueTitle(String issueTitle) {
        this.issueTitle = issueTitle;
    }

}

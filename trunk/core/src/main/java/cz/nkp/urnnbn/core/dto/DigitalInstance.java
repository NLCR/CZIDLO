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
public class DigitalInstance implements IdentifiableByLongAttribute{

    private long id;
    private long digRepId;
    private long libraryId;
    private String url;
    private DateTime published;

    public long getDigRepId() {
        return digRepId;
    }

    public void setDigRepId(long digRepId) {
        this.digRepId = digRepId;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public long getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(long libraryId) {
        this.libraryId = libraryId;
    }

    public DateTime getPublished() {
        return published;
    }

    public void setPublished(DateTime published) {
        this.published = published;
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
        final DigitalInstance other = (DigitalInstance) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.digRepId != other.digRepId) {
            return false;
        }
        if (this.libraryId != other.libraryId) {
            return false;
        }
        if ((this.url == null) ? (other.url != null) : !this.url.equals(other.url)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 17 * hash + (int) (this.digRepId ^ (this.digRepId >>> 32));
        hash = 17 * hash + (int) (this.libraryId ^ (this.libraryId >>> 32));
        hash = 17 * hash + (this.url != null ? this.url.hashCode() : 0);
        return hash;
    }
}

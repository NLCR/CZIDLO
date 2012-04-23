/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

/**
 *
 * @author Martin Řehánek
 */
public class Publication implements IdentifiableByLongAttribute {

    private Long intEntId;
    public Integer year;
    public String place;
    public String publisher;

    public Publication() {
    }

    public Publication(Publication cloneFrom) {
        this.intEntId = cloneFrom.getId();
        this.year = cloneFrom.getYear();
        this.place = cloneFrom.getPlace();
        this.publisher = cloneFrom.getPlace();
    }

    public Long getIntEntId() {
        return intEntId;
    }

    public void setIntEntId(Long intEntId) {
        this.intEntId = intEntId;
    }

    @Override
    public Long getId() {
        return getIntEntId();
    }

    @Override
    public void setId(Long id) {
        setIntEntId(id);
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Publication other = (Publication) obj;
        if (this.intEntId != other.intEntId && (this.intEntId == null || !this.intEntId.equals(other.intEntId))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.intEntId != null ? this.intEntId.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Publication{" + "intEntId=" + intEntId + '}';
    }
}

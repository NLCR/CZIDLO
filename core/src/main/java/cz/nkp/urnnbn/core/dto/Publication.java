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

    private long intEntId;
    public int year;
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

    @Override
    public long getId() {
        return getIntEntId();
    }
    
    @Override
    public void setId(long id){
        setIntEntId(id);
    }
    
    public long getIntEntId(){
        return intEntId;
    }

    public void setIntEntId(long id) {
        this.intEntId = id;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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
        if (this.intEntId != other.intEntId) {
            return false;
        }
        if (this.year != other.year) {
            return false;
        }
        if ((this.place == null) ? (other.place != null) : !this.place.equals(other.place)) {
            return false;
        }
        if ((this.publisher == null) ? (other.publisher != null) : !this.publisher.equals(other.publisher)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (int) (this.intEntId ^ (this.intEntId >>> 32));
        hash = 89 * hash + this.year;
        hash = 89 * hash + (this.place != null ? this.place.hashCode() : 0);
        hash = 89 * hash + (this.publisher != null ? this.publisher.hashCode() : 0);
        return hash;
    }
}

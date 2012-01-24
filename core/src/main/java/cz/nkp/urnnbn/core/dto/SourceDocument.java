/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

/**
 *
 * @author Martin Řehánek
 */
public class SourceDocument implements IdentifiableByLongAttribute {
    
    private long intEntId;
    private String ccnb;
    private String isbn;
    private String issn;
    private String otherId;
    private String title;
    private String periodicalVolume;
    private String periodicalNumber;
    private String publicationPlace;
    private String publisher;
    private int publicationYear;
    
    public SourceDocument() {
    }
    
    public SourceDocument(SourceDocument inserted) {
        intEntId = inserted.getIntEntId();
        ccnb = inserted.getCcnb();
        isbn = inserted.getIsbn();
        issn = inserted.getIssn();
        otherId = inserted.getOtherId();
        title = inserted.getTitle();
        periodicalVolume = inserted.getPeriodicalVolume();
        periodicalNumber = inserted.getPeriodicalNumber();
        publicationPlace = inserted.getPublicationPlace();
        publisher = inserted.getPublisher();
        publicationYear = inserted.getPublicationYear();
    }
    
    @Override
    public long getId() {
        return getIntEntId();
    }
    
    @Override
    public void setId(long id) {
        setIntEntId(intEntId);
    }
    
    public String getCcnb() {
        return ccnb;
    }
    
    public void setCcnb(String ccnb) {
        this.ccnb = ccnb;
    }
    
    public long getIntEntId() {
        return intEntId;
    }
    
    public void setIntEntId(long intEntId) {
        this.intEntId = intEntId;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public String getIssn() {
        return issn;
    }
    
    public void setIssn(String issn) {
        this.issn = issn;
    }
    
    public String getOtherId() {
        return otherId;
    }
    
    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }
    
    public String getPeriodicalNumber() {
        return periodicalNumber;
    }
    
    public void setPeriodicalNumber(String periodicalNumber) {
        this.periodicalNumber = periodicalNumber;
    }
    
    public String getPeriodicalVolume() {
        return periodicalVolume;
    }
    
    public void setPeriodicalVolume(String periodicalVolume) {
        this.periodicalVolume = periodicalVolume;
    }
    
    public String getPublicationPlace() {
        return publicationPlace;
    }
    
    public void setPublicationPlace(String publicationPlace) {
        this.publicationPlace = publicationPlace;
    }
    
    public int getPublicationYear() {
        return publicationYear;
    }
    
    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SourceDocument other = (SourceDocument) obj;
        if (this.intEntId != other.intEntId) {
            return false;
        }
        if ((this.ccnb == null) ? (other.ccnb != null) : !this.ccnb.equals(other.ccnb)) {
            return false;
        }
        if ((this.isbn == null) ? (other.isbn != null) : !this.isbn.equals(other.isbn)) {
            return false;
        }
        if ((this.issn == null) ? (other.issn != null) : !this.issn.equals(other.issn)) {
            return false;
        }
        if ((this.otherId == null) ? (other.otherId != null) : !this.otherId.equals(other.otherId)) {
            return false;
        }
        if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }
        if ((this.periodicalVolume == null) ? (other.periodicalVolume != null) : !this.periodicalVolume.equals(other.periodicalVolume)) {
            return false;
        }
        if ((this.periodicalNumber == null) ? (other.periodicalNumber != null) : !this.periodicalNumber.equals(other.periodicalNumber)) {
            return false;
        }
        if ((this.publicationPlace == null) ? (other.publicationPlace != null) : !this.publicationPlace.equals(other.publicationPlace)) {
            return false;
        }
        if ((this.publisher == null) ? (other.publisher != null) : !this.publisher.equals(other.publisher)) {
            return false;
        }
        if (this.publicationYear != other.publicationYear) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (int) (this.intEntId ^ (this.intEntId >>> 32));
        hash = 59 * hash + (this.ccnb != null ? this.ccnb.hashCode() : 0);
        hash = 59 * hash + (this.isbn != null ? this.isbn.hashCode() : 0);
        hash = 59 * hash + (this.issn != null ? this.issn.hashCode() : 0);
        hash = 59 * hash + (this.otherId != null ? this.otherId.hashCode() : 0);
        hash = 59 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 59 * hash + (this.periodicalVolume != null ? this.periodicalVolume.hashCode() : 0);
        hash = 59 * hash + (this.periodicalNumber != null ? this.periodicalNumber.hashCode() : 0);
        hash = 59 * hash + (this.publicationPlace != null ? this.publicationPlace.hashCode() : 0);
        hash = 59 * hash + (this.publisher != null ? this.publisher.hashCode() : 0);
        hash = 59 * hash + this.publicationYear;
        return hash;
    }
}

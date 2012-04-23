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

    private Long intEntId;
    private String ccnb;
    private String isbn;
    private String issn;
    private String otherId;
    private String title;
    private String volumeTitle;
    private String issueTitle;
    private String publicationPlace;
    private String publisher;
    private Integer publicationYear;

    public SourceDocument() {
    }

    public SourceDocument(SourceDocument inserted) {
        intEntId = inserted.getIntEntId();
        ccnb = inserted.getCcnb();
        isbn = inserted.getIsbn();
        issn = inserted.getIssn();
        otherId = inserted.getOtherId();
        title = inserted.getTitle();
        volumeTitle = inserted.getVolumeTitle();
        issueTitle = inserted.getIssueTitle();
        publicationPlace = inserted.getPublicationPlace();
        publisher = inserted.getPublisher();
        publicationYear = inserted.getPublicationYear();
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

    public String getCcnb() {
        return ccnb;
    }

    public void setCcnb(String ccnb) {
        this.ccnb = ccnb;
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

    public String getIssueTitle() {
        return issueTitle;
    }

    public void setIssueTitle(String issueTitle) {
        this.issueTitle = issueTitle;
    }

    public String getVolumeTitle() {
        return volumeTitle;
    }

    public void setVolumeTitle(String volumeTitle) {
        this.volumeTitle = volumeTitle;
    }

    public String getPublicationPlace() {
        return publicationPlace;
    }

    public void setPublicationPlace(String publicationPlace) {
        this.publicationPlace = publicationPlace;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
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
        if (this.intEntId != other.intEntId && (this.intEntId == null || !this.intEntId.equals(other.intEntId))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.intEntId != null ? this.intEntId.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "SourceDocument{" + "intEntId=" + intEntId + '}';
    }
}

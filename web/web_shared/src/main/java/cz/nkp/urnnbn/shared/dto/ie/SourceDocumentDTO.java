package cz.nkp.urnnbn.shared.dto.ie;

import java.io.Serializable;

import cz.nkp.urnnbn.shared.dto.PublicationDTO;

public class SourceDocumentDTO implements Serializable {

    private static final long serialVersionUID = 3649801083754028476L;
    private String title;
    private String volumeTitle;
    private String issueTitle;
    private String ccnb;
    private String isbn;
    private String issn;
    private String otherId;
    private PublicationDTO publication;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public PublicationDTO getPublication() {
        return publication;
    }

    public void setPublication(PublicationDTO publication) {
        this.publication = publication;
    }
}

package cz.nkp.urnnbn.shared.dto.ie;

import java.io.Serializable;

public class AnalyticalDTO extends IntelectualEntityDTO implements Serializable {

    private static final long serialVersionUID = -3794843838360548449L;
    private String title;
    private String subTitle;
    private String ccnb;
    private String isbn;
    private String issn;
    private String otherId;
    private SourceDocumentDTO sourceDocument;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
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
        this.isbn = normalizeIsbn(isbn);
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

    public void setSourceDocument(SourceDocumentDTO sourceDocument) {
        this.sourceDocument = sourceDocument;
    }

    public SourceDocumentDTO getSourceDocument() {
        return sourceDocument;
    }
}

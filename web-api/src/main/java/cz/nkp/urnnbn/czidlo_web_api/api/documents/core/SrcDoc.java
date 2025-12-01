package cz.nkp.urnnbn.czidlo_web_api.api.documents.core;

import cz.nkp.urnnbn.core.dto.SourceDocument;

public class SrcDoc {

    public String ccnb;
    public String isbn;
    public String issn;
    public String otherId;
    public String title;
    public String volumeTitle;
    public String issueTitle;
    public String publicationPlace;
    public String publisher;
    public Integer publicationYear;

    public static SrcDoc from(SourceDocument dto) {
        if (dto == null) {
            return null;
        }
        SrcDoc result = new SrcDoc();
        result.ccnb = dto.getCcnb();
        result.isbn = dto.getIsbn();
        result.issn = dto.getIssn();
        result.otherId = dto.getOtherId();
        result.title = dto.getTitle();
        result.volumeTitle = dto.getVolumeTitle();
        result.issueTitle = dto.getIssueTitle();
        result.publicationPlace = dto.getPublicationPlace();
        result.publisher = dto.getPublisher();
        result.publicationYear = dto.getPublicationYear();
        return result;
    }

    public String getCcnb() {
        return ccnb;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getIssn() {
        return issn;
    }

    public String getOtherId() {
        return otherId;
    }

    public String getTitle() {
        return title;
    }

    public String getVolumeTitle() {
        return volumeTitle;
    }

    public String getIssueTitle() {
        return issueTitle;
    }

    public String getPublicationPlace() {
        return publicationPlace;
    }

    public String getPublisher() {
        return publisher;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    @Override
    public String toString() {
        return "SrcDoc{" +
                "ccnb='" + ccnb + '\'' +
                ", isbn='" + isbn + '\'' +
                ", issn='" + issn + '\'' +
                ", otherId='" + otherId + '\'' +
                ", title='" + title + '\'' +
                ", volumeTitle='" + volumeTitle + '\'' +
                ", issueTitle='" + issueTitle + '\'' +
                ", publicationPlace='" + publicationPlace + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publicationYear=" + publicationYear +
                '}';
    }
}

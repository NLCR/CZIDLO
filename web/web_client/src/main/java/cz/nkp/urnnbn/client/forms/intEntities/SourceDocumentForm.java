package cz.nkp.urnnbn.client.forms.intEntities;

import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.PublicationDTO;
import cz.nkp.urnnbn.shared.dto.ie.SourceDocumentDTO;
import cz.nkp.urnnbn.client.validation.CcnbValidator;
import cz.nkp.urnnbn.client.validation.IsbnValidator;
import cz.nkp.urnnbn.client.validation.IssnValidator;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;
import cz.nkp.urnnbn.client.validation.YearValidator;

public class SourceDocumentForm extends Form {

    private final SourceDocumentDTO originalDto;

    public SourceDocumentForm() {
        this(null);
    }

    public SourceDocumentForm(SourceDocumentDTO dto) {
        this.originalDto = dto == null ? new SourceDocumentDTO() : dto;
        initForm();
    }

    @Override
    public FormFields buildFields() {
        FormFields result = new FormFields();
        Field title = new TextInputValueField(new LimitedLengthValidator(100), constants.title(), originalDto.getTitle(), true);
        result.addField("title", title);
        Field volumeTitle = new TextInputValueField(new LimitedLengthValidator(200), constants.volumeTitle(), originalDto.getVolumeTitle(), false);
        result.addField("volumeTitle", volumeTitle);
        Field issueTitle = new TextInputValueField(new LimitedLengthValidator(50), constants.issueTitle(), originalDto.getIssueTitle(), false);
        result.addField("issueTitle", issueTitle);
        Field ccnb = new TextInputValueField(new CcnbValidator(), constants.ccnb(), false);
        result.addField("ccnb", ccnb);
        Field isbn = new TextInputValueField(new IsbnValidator(), constants.isbn(), false);
        result.addField("isbn", isbn);
        Field issn = new TextInputValueField(new IssnValidator(), constants.issn(), false);
        result.addField("issn", issn);
        Field otherId = new TextInputValueField(new LimitedLengthValidator(50), constants.otherId(), originalDto.getOtherId(), false);
        result.addField("otherId", otherId);
        buildPublicationFields(result);
        return result;
    }

    private void buildPublicationFields(FormFields fields) {
        PublicationDTO publication = (originalDto.getPublication() != null) ? originalDto.getPublication() : new PublicationDTO();
        Field publisher = new TextInputValueField(new LimitedLengthValidator(50), constants.publisher(), publication.getPublisher(), false);
        fields.addField("publisher", publisher);
        Field pubPlace = new TextInputValueField(new LimitedLengthValidator(50), constants.publicationPlace(), publication.getPublicationPlace(),
                false);
        fields.addField("pubPlace", pubPlace);
        Field pubYear = new TextInputValueField(new YearValidator(), constants.publicationYear(), publication.getPublicationYear(), false);
        fields.addField("pubYear", pubYear);
    }

    @Override
    public SourceDocumentDTO getDto() {
        SourceDocumentDTO result = new SourceDocumentDTO();
        result.setTitle((String) fields.getFieldByKey("title").getInsertedValue());
        result.setVolumeTitle((String) fields.getFieldByKey("volumeTitle").getInsertedValue());
        result.setIssueTitle((String) fields.getFieldByKey("issueTitle").getInsertedValue());
        result.setCcnb((String) fields.getFieldByKey("ccnb").getInsertedValue());
        result.setIsbn((String) fields.getFieldByKey("isbn").getInsertedValue());
        result.setIssn((String) fields.getFieldByKey("issn").getInsertedValue());
        result.setOtherId((String) fields.getFieldByKey("otherId").getInsertedValue());
        result.setPublication(publicationFromForm());
        return result;
    }

    private PublicationDTO publicationFromForm() {
        PublicationDTO result = new PublicationDTO();
        result.setPublisher((String) fields.getFieldByKey("publisher").getInsertedValue());
        result.setPublicationPlace((String) fields.getFieldByKey("pubPlace").getInsertedValue());
        String yearStr = (String) fields.getFieldByKey("pubYear").getInsertedValue();
        if (yearStr != null) {
            result.setPublicationYear(Integer.valueOf(yearStr));
        }
        return result.isEmpty() ? null : result;
    }
}

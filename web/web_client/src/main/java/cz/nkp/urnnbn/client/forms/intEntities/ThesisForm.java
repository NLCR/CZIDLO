package cz.nkp.urnnbn.client.forms.intEntities;

import cz.nkp.urnnbn.client.forms.BooleanValueField;
import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.ie.PrimaryOriginatorDTO;
import cz.nkp.urnnbn.shared.dto.ie.ThesisDTO;
import cz.nkp.urnnbn.client.validation.CcnbValidator;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;

public class ThesisForm extends PublishableEntityForm {

    private final ThesisDTO dto;

    public ThesisForm(ThesisDTO dto, PrimaryOriginatorDTO originatorDto) {
        super(originatorDto);
        this.dto = dto == null ? new ThesisDTO() : dto;
        initForm();
    }

    public ThesisForm() {
        this(null, null);
    }

    @Override
    public FormFields buildFields() {
        FormFields result = new FormFields();
        Field title = new TextInputValueField(new LimitedLengthValidator(100), constants.title(), dto.getTitle(), true);
        result.addField("title", title);
        Field subTitle = new TextInputValueField(new LimitedLengthValidator(200), constants.subTitle(), dto.getSubTitle(), false);
        result.addField("subTitle", subTitle);
        Field ccnb = new TextInputValueField(new CcnbValidator(), constants.ccnb(), dto.getCcnb(), false);
        result.addField("ccnb", ccnb);
        Field otherId = new TextInputValueField(new LimitedLengthValidator(50), constants.otherId(), dto.getOtherId(), false);
        result.addField("otherId", otherId);
        Field docType = new TextInputValueField(new LimitedLengthValidator(50), constants.documentType(), dto.getDocumentType(), false);
        result.addField("docType", docType);
        Field digitalBorn = new BooleanValueField(constants.digitalBorn(), dto.isDigitalBorn());
        result.addField("digitalBorn", digitalBorn);
        addPrimaryOriginatorToFormFields(result);
        Field otherOriginator = new TextInputValueField(new LimitedLengthValidator(50), constants.otherOriginator(), dto.getOtherOriginator(), false);
        result.addField("otherOriginator", otherOriginator);
        addPublicationFieldsToFormFields(result, dto);
        Field institution = new TextInputValueField(new LimitedLengthValidator(50), constants.documentType(), dto.getDocumentType(), false);
        result.addField("institution", institution);
        return result;
    }

    @Override
    public ThesisDTO getDto() {
        ThesisDTO result = new ThesisDTO();
        result.setId(dto.getId());
        result.setTitle(getStringFieldValue("title"));
        result.setSubTitle(getStringFieldValue("subTitle"));
        result.setCcnb(getStringFieldValue("ccnb"));
        result.setOtherId(getStringFieldValue("otherId"));
        result.setDocumentType(getStringFieldValue("docType"));
        result.setDigitalBorn(getBooleanFieldValue("digitalBorn"));
        result.setOtherOriginator(getStringFieldValue("otherOriginator"));
        setPublicationDataFromFormFields(result);
        result.setDegreeAwardingInstitution(getStringFieldValue("institution"));
        setPrimaryOriginatorFromFormFields(result);
        return result;
    }
}

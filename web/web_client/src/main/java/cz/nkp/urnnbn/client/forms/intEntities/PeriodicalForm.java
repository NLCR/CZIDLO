package cz.nkp.urnnbn.client.forms.intEntities;

import cz.nkp.urnnbn.client.forms.BooleanValueField;
import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.ie.PeriodicalDTO;
import cz.nkp.urnnbn.shared.dto.ie.PrimaryOriginatorDTO;
import cz.nkp.urnnbn.client.validation.CcnbValidator;
import cz.nkp.urnnbn.client.validation.IssnValidator;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;

public class PeriodicalForm extends PublishableEntityForm {

    private final PeriodicalDTO dto;

    public PeriodicalForm(PeriodicalDTO dto, PrimaryOriginatorDTO originatorDto) {
        super(originatorDto);
        this.dto = dto == null ? new PeriodicalDTO() : dto;
        initForm();
    }

    public PeriodicalForm() {
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
        Field issn = new TextInputValueField(new IssnValidator(), constants.issn(), dto.getIssn(), false);
        result.addField("issn", issn);
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
        return result;
    }

    @Override
    public PeriodicalDTO getDto() {
        PeriodicalDTO result = new PeriodicalDTO();
        result.setId(dto.getId());
        result.setTitle((String) fields.getFieldByKey("title").getInsertedValue());
        result.setSubTitle((String) fields.getFieldByKey("subTitle").getInsertedValue());
        result.setCcnb((String) fields.getFieldByKey("ccnb").getInsertedValue());
        result.setIssn((String) fields.getFieldByKey("issn").getInsertedValue());
        result.setOtherId((String) fields.getFieldByKey("otherId").getInsertedValue());
        result.setDocumentType((String) fields.getFieldByKey("docType").getInsertedValue());
        result.setDigitalBorn((Boolean) (fields.getFieldByKey("digitalBorn").getInsertedValue()));
        result.setOtherOriginator((String) fields.getFieldByKey("otherOriginator").getInsertedValue());
        setPublicationDataFromFormFields(result);
        setPrimaryOriginatorFromFormFields(result);
        return result;
    }
}

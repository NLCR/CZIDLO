package cz.nkp.urnnbn.client.forms.intEntities;

import cz.nkp.urnnbn.client.forms.BooleanValueField;
import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.ie.PeriodicalVolumeDTO;
import cz.nkp.urnnbn.shared.dto.ie.PrimaryOriginatorDTO;
import cz.nkp.urnnbn.client.validation.CcnbValidator;
import cz.nkp.urnnbn.client.validation.IssnValidator;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;

public class PeriodicalVolumeForm extends PublishableEntityForm {

    private final PeriodicalVolumeDTO dto;

    public PeriodicalVolumeForm(PeriodicalVolumeDTO dto, PrimaryOriginatorDTO originatorDto) {
        super(originatorDto);
        this.dto = dto == null ? new PeriodicalVolumeDTO() : dto;
        initForm();
    }

    public PeriodicalVolumeForm() {
        this(null, null);
    }

    @Override
    public FormFields buildFields() {
        FormFields result = new FormFields();
        Field periodicalTitle = new TextInputValueField(new LimitedLengthValidator(100), constants.periodicalTitle(), dto.getPeriodicalTitle(), true);
        result.addField("periodicalTitle", periodicalTitle);
        Field VolumeTitle = new TextInputValueField(new LimitedLengthValidator(100), constants.periodicalVolumeTitle(), dto.getVolumeTitle(), true);
        result.addField("volumeTitle", VolumeTitle);
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
    public PeriodicalVolumeDTO getDto() {
        PeriodicalVolumeDTO result = new PeriodicalVolumeDTO();
        result.setId(dto.getId());
        result.setPeriodicalTitle(getStringFieldValue("periodicalTitle"));
        result.setVolumeTitle(getStringFieldValue("volumeTitle"));
        result.setCcnb(getStringFieldValue("ccnb"));
        result.setIssn(getStringFieldValue("issn"));
        result.setOtherId(getStringFieldValue("otherId"));
        result.setDocumentType(getStringFieldValue("docType"));
        result.setDigitalBorn(getBooleanFieldValue("digitalBorn"));
        result.setOtherOriginator(getStringFieldValue("otherOriginator"));
        setPublicationDataFromFormFields(result);
        setPrimaryOriginatorFromFormFields(result);
        return result;
    }
}

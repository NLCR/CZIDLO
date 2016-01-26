package cz.nkp.urnnbn.client.forms.intEntities;

import cz.nkp.urnnbn.client.forms.BooleanValueField;
import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.ie.PeriodicalIssueDTO;
import cz.nkp.urnnbn.shared.dto.ie.PrimaryOriginatorDTO;
import cz.nkp.urnnbn.client.validation.CcnbValidator;
import cz.nkp.urnnbn.client.validation.IssnValidator;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;

public class PeriodicalIssueForm extends PublishableEntityForm {

	private final PeriodicalIssueDTO dto;

	public PeriodicalIssueForm(PeriodicalIssueDTO dto, PrimaryOriginatorDTO originatorDto) {
		super(originatorDto);
		this.dto = dto == null ? new PeriodicalIssueDTO() : dto;
		initForm();
	}

	public PeriodicalIssueForm() {
		this(null, null);
	}

	@Override
	public FormFields buildFields() {
		FormFields result = new FormFields();
		Field periodicalTitle = new TextInputValueField(new LimitedLengthValidator(100), constants.periodicalTitle(), dto.getPeriodicalTitle(), true);
		result.addField("periodicalTitle", periodicalTitle);
		Field VolumeTitle = new TextInputValueField(new LimitedLengthValidator(100), constants.periodicalVolumeTitle(), dto.getVolumeTitle(), true);
		result.addField("volumeTitle", VolumeTitle);
		Field issueTitle = new TextInputValueField(new LimitedLengthValidator(200), constants.periodicalIssueTitle(), dto.getIssueTitle(), true);
		result.addField("issueTitle", issueTitle);
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
		Field otherOriginator = new TextInputValueField(new LimitedLengthValidator(50), constants.otherOriginator(),
				dto.getOtherOriginator(), false);
		result.addField("otherOriginator", otherOriginator);
		addPublicationFieldsToFormFields(result, dto);
		return result;
	}

	@Override
	public PeriodicalIssueDTO getDto() {
		PeriodicalIssueDTO result = new PeriodicalIssueDTO();
		result.setId(dto.getId());
		result.setPeriodicalTitle((String) fields.getFieldByKey("periodicalTitle").getInsertedValue());
		result.setVolumeTitle((String) fields.getFieldByKey("volumeTitle").getInsertedValue());
		result.setIssueTitle((String) fields.getFieldByKey("issueTitle").getInsertedValue());
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

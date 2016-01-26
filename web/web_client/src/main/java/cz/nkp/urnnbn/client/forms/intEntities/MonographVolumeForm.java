package cz.nkp.urnnbn.client.forms.intEntities;

import cz.nkp.urnnbn.client.forms.BooleanValueField;
import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.ie.MonographVolumeDTO;
import cz.nkp.urnnbn.shared.dto.ie.PrimaryOriginatorDTO;
import cz.nkp.urnnbn.client.validation.CcnbValidator;
import cz.nkp.urnnbn.client.validation.IsbnValidator;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;

public class MonographVolumeForm extends PublishableEntityForm {

	private final MonographVolumeDTO dto;

	public MonographVolumeForm(MonographVolumeDTO dto, PrimaryOriginatorDTO originatorDto) {
		super(originatorDto);
		this.dto = dto == null ? new MonographVolumeDTO() : dto;
		initForm();
	}

	public MonographVolumeForm() {
		this(null, null);
	}

	@Override
	public FormFields buildFields() {
		FormFields result = new FormFields();
		Field monTitle = new TextInputValueField(new LimitedLengthValidator(100), constants.title(), dto.getMonographTitle(), true);
		result.addField("monTitle", monTitle);
		Field volTitle = new TextInputValueField(new LimitedLengthValidator(200), constants.monographVolumeTitle(), dto.getVolumeTitle(), true);
		result.addField("volTitle", volTitle);
		Field ccnb = new TextInputValueField(new CcnbValidator(), constants.ccnb(), dto.getCcnb(), false);
		result.addField("ccnb", ccnb);
		Field isbn = new TextInputValueField(new IsbnValidator(), constants.isbn(), dto.getIsbn(), false);
		result.addField("isbn", isbn);
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
	public MonographVolumeDTO getDto() {
		MonographVolumeDTO result = new MonographVolumeDTO();
		result.setId(dto.getId());
		result.setMonographTitle((String) fields.getFieldByKey("monTitle").getInsertedValue());
		result.setVolumeTitle((String) fields.getFieldByKey("volTitle").getInsertedValue());
		result.setCcnb((String) fields.getFieldByKey("ccnb").getInsertedValue());
		result.setIsbn((String) fields.getFieldByKey("isbn").getInsertedValue());
		result.setOtherId((String) fields.getFieldByKey("otherId").getInsertedValue());
		result.setDocumentType((String) fields.getFieldByKey("docType").getInsertedValue());
		result.setDigitalBorn((Boolean) (fields.getFieldByKey("digitalBorn").getInsertedValue()));
		result.setOtherOriginator((String) fields.getFieldByKey("otherOriginator").getInsertedValue());
		setPublicationDataFromFormFields(result);
		setPrimaryOriginatorFromFormFields(result);
		return result;
	}
}

package cz.nkp.urnnbn.client.forms.intEntities;

import cz.nkp.urnnbn.client.forms.BooleanValueField;
import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.ie.AnalyticalDTO;
import cz.nkp.urnnbn.shared.dto.ie.PrimaryOriginatorDTO;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;

public class AnalyticalForm extends IntelectualEntityForm {

	private final AnalyticalDTO dto;

	public AnalyticalForm(AnalyticalDTO dto, PrimaryOriginatorDTO originatorDto) {
		super(originatorDto);
		this.dto = dto == null ? new AnalyticalDTO() : dto;
		initForm();
	}

	public AnalyticalForm() {
		this(null, null);
	}

	@Override
	public FormFields buildFields() {
		FormFields result = new FormFields();
		Field title = new TextInputValueField(new LimitedLengthValidator(100), constants.title(), dto.getTitle(), true);
		result.addField("title", title);
		Field subTitle = new TextInputValueField(new LimitedLengthValidator(200), constants.subTitle(), dto.getSubTitle(), false);
		result.addField("subTitle", subTitle);
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
		return result;
	}

	@Override
	public AnalyticalDTO getDto() {
		AnalyticalDTO result = new AnalyticalDTO();
		result.setId(dto.getId());
		result.setTitle((String) fields.getFieldByKey("title").getInsertedValue());
		result.setSubTitle((String) fields.getFieldByKey("subTitle").getInsertedValue());
		result.setOtherId((String) fields.getFieldByKey("otherId").getInsertedValue());
		result.setDocumentType((String) fields.getFieldByKey("docType").getInsertedValue());
		result.setDigitalBorn((Boolean) (fields.getFieldByKey("digitalBorn").getInsertedValue()));
		result.setOtherOriginator((String) fields.getFieldByKey("otherOriginator").getInsertedValue());
		setPrimaryOriginatorFromFormFields(result);
		return result;
	}
}

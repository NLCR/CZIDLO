package cz.nkp.urnnbn.client.forms.digitalDocument;

import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;
import cz.nkp.urnnbn.shared.validation.LimitedLengthValidator;

public class DeactivateUrnNbnForm extends Form {

	private final UrnNbnDTO originalDto;

	public DeactivateUrnNbnForm(UrnNbnDTO urn) {
		super();
		this.originalDto = urn;
		initForm();
	}

	@Override
	public FormFields buildFields() {
		FormFields result = new FormFields();
		result.addField("deactivationNote", new TextInputValueField(new LimitedLengthValidator(5), constants.note(), "", false));
		return result;
	}

	@Override
	public UrnNbnDTO getDto() {
		String deactivationNote = (String) fields.getFieldByKey("deactivationNote").getInsertedValue();
		return new UrnNbnDTO(originalDto.getCountryCode(), originalDto.getRegistrarCode(), originalDto.getDocumentCode(),
				originalDto.getDigdocId(), originalDto.isActive(), null, null, null, null, null, null, deactivationNote);
	}
}

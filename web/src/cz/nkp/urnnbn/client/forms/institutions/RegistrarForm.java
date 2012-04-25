package cz.nkp.urnnbn.client.forms.institutions;

import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.validation.LimitedLengthValidator;

public class RegistrarForm extends Form {

	private final RegistrarDTO originalRegistrar;
	private final boolean registrarCodeEditable;

	public RegistrarForm() {
		this.originalRegistrar = new RegistrarDTO();
		this.registrarCodeEditable = true;
		initForm();
	}

	public RegistrarForm(RegistrarDTO originalRegistrar) {
		this.originalRegistrar = originalRegistrar;
		this.registrarCodeEditable = false;
		initForm();
	}

	@Override
	public FormFields buildFields() {
		FormFields result = new FormFields();
		Field name = new TextInputValueField(new LimitedLengthValidator(100), constants.title(), originalRegistrar.getName(), true);
		result.addField("name", name);
		Field code = new TextInputValueField(new LimitedLengthValidator(6), constants.code(), originalRegistrar.getCode(), true);
		result.addField("code", code);
		if (!registrarCodeEditable) {
			code.disable();
		}
		Field description = new TextInputValueField(new LimitedLengthValidator(100), constants.description(),
				originalRegistrar.getDescription(), false);
		result.addField("description", description);
		return result;
	}

	@Override
	public RegistrarDTO getDto() {
		RegistrarDTO result = new RegistrarDTO();
		result.setId(originalRegistrar.getId());
		result.setCode((String) fields.getFieldByKey("code").getInsertedValue());
		result.setName((String) fields.getFieldByKey("name").getInsertedValue());
		result.setDescription((String) fields.getFieldByKey("description").getInsertedValue());
		return result;
	}

}

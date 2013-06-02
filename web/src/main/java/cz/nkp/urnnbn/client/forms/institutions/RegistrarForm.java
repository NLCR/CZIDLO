package cz.nkp.urnnbn.client.forms.institutions;

import cz.nkp.urnnbn.client.forms.BooleanValueField;
import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.LabelField;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.validation.LimitedLengthValidator;
import cz.nkp.urnnbn.shared.validation.PositiveIntegerValidator;

public class RegistrarForm extends Form {

	private final RegistrarDTO originalRegistrar;
	private final boolean registrarCodeEditable;

	public RegistrarForm() {
		this.originalRegistrar = new RegistrarDTO();
		originalRegistrar.setRegModeByResolverAllowed(true);
		originalRegistrar.setRegModeByRegistrarAllowed(false);
		originalRegistrar.setRegModeByReservationAllowed(false);
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
		Field code = new TextInputValueField(new LimitedLengthValidator(2, 6), constants.code(), originalRegistrar.getCode(), true);
		result.addField("code", code);
		if (!registrarCodeEditable) {
			code.disable();
		}
		Field description = new TextInputValueField(new LimitedLengthValidator(100), constants.description(),
				originalRegistrar.getDescription(), false);
		result.addField("description", description);
		// mody registrace
		result.addField("modesLabel", new LabelField(constants.allowedRegistrationModes()));
		result.addField("modeByResolver", new BooleanValueField(constants.modeByResolver(), originalRegistrar.isRegModeByResolverAllowed()));
		result.addField("modeByReservation",
				new BooleanValueField(constants.modeByReservation(), originalRegistrar.isRegModeByReservationAllowed()));
		result.addField("modeByRegistrar",
				new BooleanValueField(constants.modeByRegistrar(), originalRegistrar.isRegModeByRegistrarAllowed()));
		// priority and visibility
		result.addField("priority", new TextInputValueField(new PositiveIntegerValidator(), constants.priority(), 1, false));
		result.addField("hidden", new BooleanValueField(constants.hidden(), false));
		return result;
	}

	@Override
	public RegistrarDTO getDto() {
		RegistrarDTO result = new RegistrarDTO();
		result.setId(originalRegistrar.getId());
		result.setCode((String) fields.getFieldByKey("code").getInsertedValue());
		result.setName((String) fields.getFieldByKey("name").getInsertedValue());
		result.setDescription((String) fields.getFieldByKey("description").getInsertedValue());
		result.setRegModeByResolverAllowed((Boolean)fields.getFieldByKey("modeByResolver").getInsertedValue());
		result.setRegModeByReservationAllowed((Boolean)fields.getFieldByKey("modeByReservation").getInsertedValue());
		result.setRegModeByRegistrarAllowed((Boolean)fields.getFieldByKey("modeByRegistrar").getInsertedValue());
		return result;
	}

}

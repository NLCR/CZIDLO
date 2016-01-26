package cz.nkp.urnnbn.client.forms.userAccounts;

import cz.nkp.urnnbn.client.accounts.PasswordGenerator;
import cz.nkp.urnnbn.client.forms.BooleanValueField;
import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO.ROLE;
import cz.nkp.urnnbn.client.validation.EmailValidator;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;
import cz.nkp.urnnbn.client.validation.PasswordValidator;
import cz.nkp.urnnbn.client.validation.Validator;

public class UserDetailsForm extends Form {

	private final UserDTO originalUser;
	private final boolean editForm;// otherwise insert form

	public UserDetailsForm() {
		this(null);
	}

	public UserDetailsForm(UserDTO originalUser) {
		if (originalUser == null) {
			this.originalUser = new UserDTO();
			editForm = false;
		} else {
			this.originalUser = originalUser;
			editForm = true;
		}
		initForm();
	}

	@Override
	public FormFields buildFields() {
		FormFields result = new FormFields();
		Field login = new TextInputValueField(new LimitedLengthValidator(100), constants.login(), originalUser.getLogin(), true);
		result.addField("login", login);
		if (!editForm) {
			Validator validator = new PasswordValidator(5, 10);
			String generatedPassword = new PasswordGenerator((PasswordValidator) validator).generatePassword();
			Field passwordField = new TextInputValueField(validator, constants.password(), generatedPassword, true);
			result.addField("password", passwordField);
		}
		Field email = new TextInputValueField(new EmailValidator(), constants.email(), originalUser.getEmail(), true);
		result.addField("email", email);
		Field admin = new BooleanValueField(constants.administrator(), originalUser.getRole() == ROLE.SUPER_ADMIN);
		result.addField("admin", admin);
		if (editForm) {
			login.disable();
		}
		return result;
	}

	@Override
	public UserDTO getDto() {
		UserDTO result = new UserDTO();
		result.setId(originalUser.getId());
		result.setLogin((String) fields.getFieldByKey("login").getInsertedValue());
		if (!editForm) {
			result.setPassword((String) fields.getFieldByKey("password").getInsertedValue());
		}
		result.setEmail((String) fields.getFieldByKey("email").getInsertedValue());

		Boolean isAdmin = (Boolean) fields.getFieldByKey("admin").getInsertedValue();
		if (isAdmin == null || isAdmin == false) {
			result.setRole(ROLE.ADMIN);
		} else {
			result.setRole(ROLE.SUPER_ADMIN);
		}
		return result;
	}
}

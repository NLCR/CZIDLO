package cz.nkp.urnnbn.client.forms.userAccounts;

import cz.nkp.urnnbn.client.accounts.PasswordGenerator;
import cz.nkp.urnnbn.client.forms.*;
import cz.nkp.urnnbn.client.validation.EmailValidator;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;
import cz.nkp.urnnbn.client.validation.PasswordValidator;
import cz.nkp.urnnbn.client.validation.Validator;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO.ROLE;

public class UserAddForm extends Form {

    private static final String FIELD_LOGIN = "login";
    private static final String FIELD_PASSWORD = "password";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_ADMIN = "admin";

    public UserAddForm() {
        initForm();
    }

    @Override
    public FormFields buildFields() {
        FormFields result = new FormFields();
        Field login = new TextInputValueField(new LimitedLengthValidator(100), constants.login(), null, true);
        result.addField(FIELD_LOGIN, login);
        //password
        Validator passValidator = new PasswordValidator(5, 10);
        String generatedPassword = new PasswordGenerator((PasswordValidator) passValidator).generatePassword();
        Field passwordField = new TextInputValueField(passValidator, constants.password(), generatedPassword, true);
        result.addField(FIELD_PASSWORD, passwordField);
        //email
        Field email = new TextInputValueField(new EmailValidator(), constants.email(), null, true);
        result.addField(FIELD_EMAIL, email);
        //admin
        Field admin = new BooleanValueField(constants.administrator(), false);
        result.addField(FIELD_ADMIN, admin);
        return result;
    }

    @Override
    public UserDTO getDto() {
        UserDTO result = new UserDTO();
        result.setLogin(getStringFieldValue(FIELD_LOGIN));
        result.setPassword(getStringFieldValue(FIELD_PASSWORD));
        result.setEmail(getStringFieldValue(FIELD_EMAIL));
        Boolean isAdmin = getBooleanFieldValue(FIELD_ADMIN);
        if (isAdmin) {
            result.setRole(ROLE.SUPER_ADMIN);
        } else {
            result.setRole(ROLE.ADMIN);
        }
        return result;
    }
}

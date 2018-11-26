package cz.nkp.urnnbn.client.forms.userAccounts;

import cz.nkp.urnnbn.client.accounts.PasswordGenerator;
import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;
import cz.nkp.urnnbn.client.validation.PasswordValidator;
import cz.nkp.urnnbn.client.validation.Validator;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class ChangeUserPasswordByAdminForm extends Form {

    private static final String FIELD_LOGIN = "login";
    private static final String FIELD_PASS_NEW = "passNew";
    private final UserDTO user;

    public ChangeUserPasswordByAdminForm(UserDTO user) {
        this.user = user;
        initForm();
    }

    @Override
    public FormFields buildFields() {
        FormFields result = new FormFields();
        //login
        Field login = new TextInputValueField(new LimitedLengthValidator(100), constants.login(), user.getLogin(), true);
        login.disable();
        result.addField(FIELD_LOGIN, login);
        //passwords
        Validator passwordValidator = new PasswordValidator(5, 10);
        String generatedPassword = new PasswordGenerator((PasswordValidator) passwordValidator).generatePassword();
        Field passNewField = new TextInputValueField(passwordValidator, constants.changePasswordDialogNewPassword(), generatedPassword, true);
        result.addField(FIELD_PASS_NEW, passNewField);
        passNewField.disable();
        return result;
    }

    @Override
    public UserDTO getDto() {
        UserDTO result = new UserDTO(user);
        result.setPassword(getStringFieldValue(FIELD_PASS_NEW));
        return result;
    }

}

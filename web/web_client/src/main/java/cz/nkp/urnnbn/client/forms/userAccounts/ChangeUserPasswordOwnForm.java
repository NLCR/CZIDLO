package cz.nkp.urnnbn.client.forms.userAccounts;

import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;
import cz.nkp.urnnbn.client.validation.PasswordValidator;
import cz.nkp.urnnbn.client.validation.Validator;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class ChangeUserPasswordOwnForm extends Form {

    private static final String FIELD_LOGIN = "login";
    private static final String FIELD_PASS_OLD = "passOld";
    private static final String FIELD_PASS_NEW1 = "passNew1";
    private static final String FIELD_PASS_NEW2 = "passNew2";
    private final UserDTO user;

    public ChangeUserPasswordOwnForm(UserDTO user) {
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
        Field passOriginalField = new TextInputValueField(passwordValidator, constants.changePasswordDialogCurrentPassword(), null, true);
        result.addField(FIELD_PASS_OLD, passOriginalField);
        Field passNew1Field = new TextInputValueField(passwordValidator, constants.changePasswordDialogNewPassword(), null, true);
        result.addField(FIELD_PASS_NEW1, passNew1Field);
        Field passNew2Field = new TextInputValueField(passwordValidator, constants.changePasswordDialogNewPasswordAgain(), null, true);
        result.addField(FIELD_PASS_NEW2, passNew2Field);
        return result;
    }

    @Override
    public UserDTO getDto() {
        UserDTO result = new UserDTO(user);
        result.setPassword(getStringFieldValue(FIELD_PASS_NEW1));
        return result;
    }

    public String getPassOld() {
        return getStringFieldValue(FIELD_PASS_OLD);
    }

    public String getPassNew1() {
        return getStringFieldValue(FIELD_PASS_NEW1);
    }

    public String getPassNew2() {
        return getStringFieldValue(FIELD_PASS_NEW2);
    }


}

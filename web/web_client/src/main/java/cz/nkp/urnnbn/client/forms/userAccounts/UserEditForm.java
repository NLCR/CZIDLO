package cz.nkp.urnnbn.client.forms.userAccounts;

import cz.nkp.urnnbn.client.forms.*;
import cz.nkp.urnnbn.client.validation.EmailValidator;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO.ROLE;

public class UserEditForm extends Form {

    private static final String FIELD_LOGIN = "login";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_ADMIN = "admin";

    private final UserDTO originalUser;

    public UserEditForm(UserDTO originalUser) {
        this.originalUser = originalUser;
        initForm();
    }

    @Override
    public FormFields buildFields() {
        FormFields result = new FormFields();
        Field login = new TextInputValueField(new LimitedLengthValidator(100), constants.login(), originalUser.getLogin(), true);
        result.addField(FIELD_LOGIN, login);
        login.disable();
        Field email = new TextInputValueField(new EmailValidator(), constants.email(), originalUser.getEmail(), true);
        result.addField(FIELD_EMAIL, email);
        Field admin = new BooleanValueField(constants.administrator(), originalUser.getRole() == ROLE.SUPER_ADMIN);
        result.addField(FIELD_ADMIN, admin);
        return result;
    }

    @Override
    public UserDTO getDto() {
        UserDTO result = new UserDTO();
        result.setId(originalUser.getId());
        result.setLogin(originalUser.getLogin());
        result.setEmail(getStringFieldValue(FIELD_EMAIL));
        Boolean isAdmin = getBooleanFieldValue(FIELD_ADMIN);
        if (isAdmin == null || isAdmin == false) {
            result.setRole(ROLE.ADMIN);
        } else {
            result.setRole(ROLE.SUPER_ADMIN);
        }
        return result;
    }
}

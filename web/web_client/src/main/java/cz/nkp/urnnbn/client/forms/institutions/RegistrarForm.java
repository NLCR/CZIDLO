package cz.nkp.urnnbn.client.forms.institutions;

import cz.nkp.urnnbn.client.forms.*;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;

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
        Field description = new TextInputValueField(new LimitedLengthValidator(100), constants.description(), originalRegistrar.getDescription(),
                false);
        result.addField("description", description);
        // mody registrace
        result.addField("modesLabel", new LabelField(constants.allowedRegistrationModes()));
        result.addField("modeByResolver", new BooleanValueField(constants.modeByResolver(), originalRegistrar.isRegModeByResolverAllowed()));
        result.addField("modeByReservation", new BooleanValueField(constants.modeByReservation(), originalRegistrar.isRegModeByReservationAllowed()));
        result.addField("modeByRegistrar", new BooleanValueField(constants.modeByRegistrar(), originalRegistrar.isRegModeByRegistrarAllowed()));
        return result;
    }

    @Override
    public RegistrarDTO getDto() {
        RegistrarDTO result = new RegistrarDTO();
        result.setId(originalRegistrar.getId());
        result.setCode(getStringFieldValue("code"));
        result.setName(getStringFieldValue("name"));
        result.setDescription(getStringFieldValue("description"));
        result.setRegModeByResolverAllowed(getBooleanFieldValue("modeByResolver"));
        result.setRegModeByReservationAllowed(getBooleanFieldValue("modeByReservation"));
        result.setRegModeByRegistrarAllowed(getBooleanFieldValue("modeByRegistrar"));
        if (originalRegistrar != null) {
            result.setHidden(originalRegistrar.isHidden());
        }
        return result;
    }

}

package cz.nkp.urnnbn.client.forms.institutions;

import cz.nkp.urnnbn.client.forms.BooleanValueField;
import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;

public class ArchiverVisibilityForm extends Form {

    private final ArchiverDTO originalArchiver;

    public ArchiverVisibilityForm(ArchiverDTO originalArchiver) {
        this.originalArchiver = originalArchiver;
        initForm();
    }

    @Override
    public FormFields buildFields() {
        FormFields result = new FormFields();
        result.addField("hidden", new BooleanValueField(constants.hidden(), originalArchiver.isHidden()));
        return result;
    }

    @Override
    public ArchiverDTO getDto() {
        originalArchiver.setHidden(getBooleanFieldValue("hidden"));
        return originalArchiver;
    }

}

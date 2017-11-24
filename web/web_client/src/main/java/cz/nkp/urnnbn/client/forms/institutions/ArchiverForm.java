package cz.nkp.urnnbn.client.forms.institutions;

import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;

public class ArchiverForm extends Form {

    private final ArchiverDTO originalArchiver;

    public ArchiverForm() {
        this(null);
    }

    public ArchiverForm(ArchiverDTO originalArchiver) {
        if (originalArchiver == null) {
            originalArchiver = new ArchiverDTO();
        }
        this.originalArchiver = originalArchiver;
        initForm();
    }

    @Override
    public FormFields buildFields() {
        FormFields result = new FormFields();
        result.addField("name", new TextInputValueField(new LimitedLengthValidator(100), constants.title(), originalArchiver.getName(), true));
        result.addField("description",
                new TextInputValueField(new LimitedLengthValidator(100), constants.description(), originalArchiver.getDescription(), false));
        return result;
    }

    @Override
    public ArchiverDTO getDto() {
        ArchiverDTO result = new ArchiverDTO();
        result.setId(originalArchiver.getId());
        result.setName(getStringFieldValue("name"));
        result.setDescription(getStringFieldValue("description"));
        if (originalArchiver != null) {
            result.setHidden(originalArchiver.isHidden());
        }
        return result;
    }
}

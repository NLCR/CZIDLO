package cz.nkp.urnnbn.client.forms.digitalDocument;

import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;

public class DeactivateUrnNbnForm extends Form {

    private final UrnNbnDTO originalDto;

    public DeactivateUrnNbnForm(UrnNbnDTO urn) {
        super();
        this.originalDto = urn;
        initForm();
    }

    @Override
    public FormFields buildFields() {
        FormFields result = new FormFields();
        result.addField("deactivationNote", new TextInputValueField(new LimitedLengthValidator(100), constants.note(), "", false, 500));
        return result;
    }

    @Override
    public UrnNbnDTO getDto() {
        String deactivationNote = getStringFieldValue("deactivationNote");
        return new UrnNbnDTO(originalDto.getCountryCode(), originalDto.getRegistrarCode(), originalDto.getDocumentCode(), originalDto.getDigdocId(),
                originalDto.isActive(), null, null, null, null, null, null, deactivationNote);
    }
}

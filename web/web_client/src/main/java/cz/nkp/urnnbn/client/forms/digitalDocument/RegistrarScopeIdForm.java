package cz.nkp.urnnbn.client.forms.digitalDocument;

import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;
import cz.nkp.urnnbn.shared.dto.RegistrarScopeIdDTO;

public class RegistrarScopeIdForm extends Form {

    private final RegistrarScopeIdDTO originalDto;
    private final boolean fixType;

    public RegistrarScopeIdForm(Long registrarId, Long digDocId) {
        this(buildDto(registrarId, digDocId), false);
    }

    private static RegistrarScopeIdDTO buildDto(Long registrarId, Long digDocId) {
        RegistrarScopeIdDTO dto = new RegistrarScopeIdDTO();
        dto.setRegistrarId(registrarId);
        dto.setDigDocId(digDocId);
        return dto;
    }

    public RegistrarScopeIdForm(RegistrarScopeIdDTO originalDto, boolean fixType) {
        this.originalDto = originalDto;
        this.fixType = fixType;
        initForm();
    }

    @Override
    public FormFields buildFields() {
        FormFields fields = new FormFields();
        Field typeField = new TextInputValueField(new LimitedLengthValidator(100), constants.idType(), originalDto.getType(), true);
        if (fixType) {
            typeField.disable();
        }
        fields.addField("type", typeField);
        fields.addField("value", new TextInputValueField(new LimitedLengthValidator(100), constants.idValue(), originalDto.getValue(), true));
        return fields;
    }

    @Override
    public RegistrarScopeIdDTO getDto() {
        RegistrarScopeIdDTO dto = new RegistrarScopeIdDTO();
        dto.setRegistrarId(originalDto.getRegistrarId());
        dto.setDigDocId(originalDto.getDigDocId());
        dto.setType(getStringFieldValue("type"));
        dto.setValue(getStringFieldValue("value"));
        return dto;
    }
}

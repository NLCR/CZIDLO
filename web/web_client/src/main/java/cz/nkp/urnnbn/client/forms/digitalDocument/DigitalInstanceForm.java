package cz.nkp.urnnbn.client.forms.digitalDocument;

import cz.nkp.urnnbn.client.forms.*;
import cz.nkp.urnnbn.client.validation.LimitedLengthUrlValidator;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;
import cz.nkp.urnnbn.shared.dto.DigitalLibraryDTO;

import java.util.ArrayList;

public class DigitalInstanceForm extends Form {

    private final DigitalInstanceDTO originalDto;
    private final ArrayList<DigitalLibraryDTO> libraries;

    public DigitalInstanceForm(ArrayList<DigitalLibraryDTO> libraries) {
        this(null, libraries);
    }

    public DigitalInstanceForm(DigitalInstanceDTO originalDto, ArrayList<DigitalLibraryDTO> libraries) {
        this.originalDto = originalDto != null ? originalDto : new DigitalInstanceDTO();
        this.libraries = libraries;
        initForm();
    }

    @Override
    public FormFields buildFields() {
        FormFields fields = new FormFields();
        fields.addField("library", new DigitalLibraryListField(libraries));
        fields.addField("format", new TextInputValueField(new LimitedLengthValidator(100), constants.format(), originalDto.getFormat(), false));
        fields.addField("access", new TextInputValueField(new LimitedLengthValidator(100), constants.accessibility(), originalDto.getAccessibility(), false));
        fields.addField("url", new TextInputValueField(new LimitedLengthUrlValidator(100), constants.url(), originalDto.getUrl(), true));
        fields.addField("accessRestriction", new AccessRestrictionField(originalDto.getAccessRestriction()));
        return fields;
    }

    @Override
    public DigitalInstanceDTO getDto() {
        DigitalInstanceDTO dto = new DigitalInstanceDTO();
        dto.setId(originalDto.getId());
        dto.setLibrary((DigitalLibraryDTO) fields.getFieldByKey("library").getInsertedValue());
        dto.setFormat(getStringFieldValue("format"));
        dto.setAccessibility(getStringFieldValue("access"));
        dto.setAccessRestriction(originalDto.getAccessRestriction());
        dto.setUrl(getStringFieldValue("url"));
        dto.setAccessRestriction((DigitalInstanceDTO.ACCESS_RESTRICTION) fields.getFieldByKey("accessRestriction").getInsertedValue());
        dto.setActive(true);
        return dto;
    }
}

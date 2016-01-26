package cz.nkp.urnnbn.client.forms.digitalDocument;

import java.util.ArrayList;

import cz.nkp.urnnbn.client.forms.DigitalLibraryListField;
import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;
import cz.nkp.urnnbn.shared.dto.DigitalLibraryDTO;
import cz.nkp.urnnbn.shared.validation.LimitedLengthUrlValidator;
import cz.nkp.urnnbn.shared.validation.LimitedLengthValidator;

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
		FormFields result = new FormFields();
		result.addField("library", new DigitalLibraryListField(libraries));
		result.addField("format", new TextInputValueField(new LimitedLengthValidator(100), constants.format(), originalDto.getFormat(),
				false));
		result.addField("access",
				new TextInputValueField(new LimitedLengthValidator(100), constants.accessibility(), originalDto.getAccessibility(), false));
		result.addField("url", new TextInputValueField(new LimitedLengthUrlValidator(100), constants.url(), originalDto.getUrl(), true));
		return result;
	}

	@Override
	public DigitalInstanceDTO getDto() {
		DigitalInstanceDTO result = new DigitalInstanceDTO();
		result.setId(originalDto.getId());
		result.setLibrary((DigitalLibraryDTO) fields.getFieldByKey("library").getInsertedValue());
		result.setFormat((String) fields.getFieldByKey("format").getInsertedValue());
		result.setAccessibility((String) fields.getFieldByKey("access").getInsertedValue());
		result.setUrl((String) fields.getFieldByKey("url").getInsertedValue());
		result.setActive(true);
		return result;
	}
}

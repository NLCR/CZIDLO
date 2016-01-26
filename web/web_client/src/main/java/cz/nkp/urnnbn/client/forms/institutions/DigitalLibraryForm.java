package cz.nkp.urnnbn.client.forms.institutions;

import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.DigitalLibraryDTO;
import cz.nkp.urnnbn.client.validation.LimitedLengthUrlValidator;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;

public class DigitalLibraryForm extends Form {

	private final DigitalLibraryDTO originalLibrary;

	public DigitalLibraryForm() {
		this(null);
	}

	public DigitalLibraryForm(DigitalLibraryDTO library) {
		if (library == null) {
			library = new DigitalLibraryDTO();
		}
		this.originalLibrary = library;
		initForm();
	}

	@Override
	public FormFields buildFields() {
		FormFields result = new FormFields();
		Field name = new TextInputValueField(new LimitedLengthValidator(100), constants.title(), originalLibrary.getName(), true);
		result.addField("name", name);
		Field description = new TextInputValueField(new LimitedLengthValidator(100), constants.description(),
				originalLibrary.getDescription(), false);
		result.addField("description", description);
		Field url = new TextInputValueField(new LimitedLengthUrlValidator(100), constants.url(), originalLibrary.getUrl(), false);
		result.addField("url", url);
		return result;
	}

	@Override
	public DigitalLibraryDTO getDto() {
		DigitalLibraryDTO result = new DigitalLibraryDTO();
		result.setId(originalLibrary.getId());
		result.setName((String) fields.getFieldByKey("name").getInsertedValue());
		result.setDescription((String) fields.getFieldByKey("description").getInsertedValue());
		result.setUrl((String) fields.getFieldByKey("url").getInsertedValue());
		return result;
	}

}

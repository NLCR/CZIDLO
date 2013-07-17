package cz.nkp.urnnbn.client.forms.institutions;

import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.client.forms.IntegerInputValueField;
import cz.nkp.urnnbn.client.forms.BooleanValueField;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;
import cz.nkp.urnnbn.shared.validation.LimitedLengthValidator;
import cz.nkp.urnnbn.shared.validation.PositiveIntegerValidator;

public class ArchiverForm extends Form {

	private final ArchiverDTO originalArchiver;

	public ArchiverForm() {
		this(null);
	}

	public ArchiverForm(ArchiverDTO originalArchiver) {
		if (originalArchiver == null) {
			originalArchiver = new ArchiverDTO();
			originalArchiver.setOrder(0L);
		}
		this.originalArchiver = originalArchiver;
		initForm();
	}

	@Override
	public FormFields buildFields() {
		FormFields result = new FormFields();
		result.addField("name", new TextInputValueField(new LimitedLengthValidator(100), constants.title(), originalArchiver.getName(),
				true));
		result.addField("description",
				new TextInputValueField(new LimitedLengthValidator(100), constants.description(), originalArchiver.getDescription(), false));
		result.addField("order", new TextInputValueField(new PositiveIntegerValidator(), constants.order(), originalArchiver.getOrder().toString(), true));
		result.addField("hidden", new BooleanValueField(constants.hidden(), originalArchiver.isHidden()));
		return result;
	}

	@Override
	public ArchiverDTO getDto() {
		ArchiverDTO result = new ArchiverDTO();
		result.setId(originalArchiver.getId());
		result.setName((String) fields.getFieldByKey("name").getInsertedValue());
		result.setDescription((String) fields.getFieldByKey("description").getInsertedValue());
		result.setOrder(Long.valueOf((String) fields.getFieldByKey("order").getInsertedValue()));
		result.setHidden((Boolean) fields.getFieldByKey("hidden").getInsertedValue());
		return result;
	}
}
package cz.nkp.urnnbn.client.forms.institutions;

import cz.nkp.urnnbn.client.forms.BooleanValueField;
import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;
import cz.nkp.urnnbn.shared.validation.PositiveIntegerValidator;

public class ArchiverVisibilityAndOrderForm extends Form {

	private final ArchiverDTO originalArchiver;

	public ArchiverVisibilityAndOrderForm(ArchiverDTO originalArchiver) {
		this.originalArchiver = originalArchiver;
		initForm();
	}
	
	@Override
	public FormFields buildFields() {
		FormFields result = new FormFields();
		result.addField("order", new TextInputValueField(new PositiveIntegerValidator(), constants.order(), originalArchiver.getOrder().toString(), true));
		result.addField("hidden", new BooleanValueField(constants.hidden(), originalArchiver.isHidden()));
		return result;
	}

	@Override
	public ArchiverDTO getDto() {
		originalArchiver.setOrder(Long.valueOf((String) fields.getFieldByKey("order").getInsertedValue()));
		originalArchiver.setHidden((Boolean) fields.getFieldByKey("hidden").getInsertedValue());
		return originalArchiver;
	}

}

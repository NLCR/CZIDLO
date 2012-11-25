package cz.nkp.urnnbn.client.forms.digitalDocument;

import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;
import cz.nkp.urnnbn.shared.validation.UrnNbnPartCValidator;

public class UrnNbnForm extends Form {

	private final RegistrarDTO registrar;
	private final String countryCode;

	public UrnNbnForm(RegistrarDTO registrar, String countryCode) {
		this.countryCode = countryCode;
		this.registrar = registrar;
		initForm();
	}

	@Override
	public FormFields buildFields() {
		FormFields result = new FormFields();
		Field urnNbn = new TextInputValueField(new UrnNbnPartCValidator(), buildPrefix(), true, false);
		result.addField("urnNbn", urnNbn);
		return result;
	}

	String buildPrefix() {
		return "urn:nbn:" + countryCode + ":" + registrar.getCode() + "-";
	}

	@Override
	public UrnNbnDTO getDto() {
		String documentCode = (String) fields.getFieldByKey("urnNbn").getInsertedValue();
		return new UrnNbnDTO(countryCode, registrar.getCode(), documentCode, null, true, null, null, null, null);
	}
}

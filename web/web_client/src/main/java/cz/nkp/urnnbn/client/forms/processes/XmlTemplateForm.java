package cz.nkp.urnnbn.client.forms.processes;

import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTO;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTOType;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;

public class XmlTemplateForm extends Form {

	private final XmlTransformationDTOType type;

	public XmlTemplateForm(XmlTransformationDTOType type) {
		this.type = type;
		initForm();
	}

	@Override
	public FormFields buildFields() {
		FormFields result = new FormFields();
		result.addField("name", new TextInputValueField(new LimitedLengthValidator(1, 20), constants.processOaiAdapterTransformationTitle(),"", true));
		result.addField("description", new TextInputValueField(new LimitedLengthValidator(30), constants.processOaiAdapterTransformationDescription(), "", false));
		result.addField("file", new UploadFileField(constants.processOaiAdapterTransformationXslt()));
		return result;
	}

	@Override
	public XmlTransformationDTO getDto() {
		XmlTransformationDTO result = new XmlTransformationDTO();
		result.setType(type);
		result.setName((String) fields.getFieldByKey("name").getInsertedValue());
		result.setDescription((String) fields.getFieldByKey("description").getInsertedValue());
		result.setTemplateTemporaryFile((String) fields.getFieldByKey("file").getInsertedValue());
		return result;
	}

}

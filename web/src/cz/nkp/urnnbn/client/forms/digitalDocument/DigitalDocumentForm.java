package cz.nkp.urnnbn.client.forms.digitalDocument;

import java.util.ArrayList;

import cz.nkp.urnnbn.client.forms.ArchiverListField;
import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.validation.LimitedLengthValidator;

public class DigitalDocumentForm extends Form {

	private final DigitalDocumentDTO originalDto;
	private final ArrayList<ArchiverDTO> archivers;
	private final ArchiverDTO selectedArchiver;

	public DigitalDocumentForm(ArrayList<ArchiverDTO> archivers) {
		this(null, archivers, null);
	}

	public DigitalDocumentForm(DigitalDocumentDTO originalDto, ArrayList<ArchiverDTO> archivers, ArchiverDTO selectedArchiver) {
		this.originalDto = originalDto == null ? new DigitalDocumentDTO() : originalDto;
		this.archivers = archivers;
		this.selectedArchiver = selectedArchiver;
		initForm();
	}

	@Override
	public FormFields buildFields() {
		FormFields result = new FormFields();
		Field archiver = new ArchiverListField(archivers, selectedArchiver);
		result.addField("archiver", archiver);
		Field financed = new TextInputValueField(new LimitedLengthValidator(100), constants.financed(), originalDto.getFinanced(), false);
		result.addField("financed", financed);
		// TODO: co s urn:nbn?
		// TODO: registrar scope identifiers
		Field contractNumber = new TextInputValueField(new LimitedLengthValidator(100), constants.contractNumber(),
				originalDto.getContractNumber(), false);
		result.addField("contractNumber", contractNumber);
		return result;
	}

	@Override
	public DigitalDocumentDTO getDto() {
		DigitalDocumentDTO result = new DigitalDocumentDTO();
		result.setId(originalDto.getId());
		result.setRegistrar(originalDto.getRegistrar());
		result.setArchiver((ArchiverDTO) fields.getFieldByKey("archiver").getInsertedValue());
		result.setFinanced((String) fields.getFieldByKey("financed").getInsertedValue());
		result.setContractNumber((String) fields.getFieldByKey("contractNumber").getInsertedValue());
		result.setArchiver((ArchiverDTO) fields.getFieldByKey("archiver").getInsertedValue());
		return result;
	}
}

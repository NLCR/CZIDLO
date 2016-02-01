package cz.nkp.urnnbn.client.forms.digitalDocument;

import java.util.ArrayList;

import cz.nkp.urnnbn.client.forms.ArchiverListField;
import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;

public class DigitalDocumentForm extends Form {

    private final DigitalDocumentDTO originalDto;
    private final ArrayList<ArchiverDTO> archivers;
    private final ArchiverDTO selectedArchiver;
    private final RegistrarDTO registrar;

    public DigitalDocumentForm(RegistrarDTO registrar, ArrayList<ArchiverDTO> archivers) {
        this(null, registrar, archivers, null);
    }

    public DigitalDocumentForm(DigitalDocumentDTO originalDto, RegistrarDTO registrar, ArrayList<ArchiverDTO> archivers, ArchiverDTO selectedArchiver) {
        this.originalDto = originalDto == null ? new DigitalDocumentDTO() : originalDto;
        this.registrar = registrar;
        this.archivers = archivers;
        this.selectedArchiver = selectedArchiver;
        initForm();
    }

    @Override
    public FormFields buildFields() {
        FormFields result = new FormFields();
        result.addField("archiver", new ArchiverListField(registrar, archivers, selectedArchiver));
        result.addField("financed", new TextInputValueField(new LimitedLengthValidator(100), constants.financed(), originalDto.getFinanced(), false));
        // TODO: registrar scope identifiers
        result.addField("contractNumber",
                new TextInputValueField(new LimitedLengthValidator(100), constants.contractNumber(), originalDto.getContractNumber(), false));
        return result;
    }

    @Override
    public DigitalDocumentDTO getDto() {
        DigitalDocumentDTO result = new DigitalDocumentDTO();
        result.setId(originalDto.getId());
        result.setIntEntId(originalDto.getIntEntId());
        result.setRegistrar(originalDto.getRegistrar());
        result.setArchiver((ArchiverDTO) fields.getFieldByKey("archiver").getInsertedValue());
        result.setFinanced((String) fields.getFieldByKey("financed").getInsertedValue());
        result.setContractNumber((String) fields.getFieldByKey("contractNumber").getInsertedValue());
        result.setArchiver((ArchiverDTO) fields.getFieldByKey("archiver").getInsertedValue());
        return result;
    }
}

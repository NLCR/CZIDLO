package cz.nkp.urnnbn.client.forms.intEntities;

import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.Form;
import cz.nkp.urnnbn.client.forms.FormFields;
import cz.nkp.urnnbn.client.forms.PrimaryOriginatorField;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import cz.nkp.urnnbn.shared.dto.ie.PrimaryOriginatorDTO;
import cz.nkp.urnnbn.shared.dto.ie.PrimaryOriginatorType;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;

public abstract class IntelectualEntityForm extends Form {

    private final PrimaryOriginatorDTO originatorDto;

    public IntelectualEntityForm(PrimaryOriginatorDTO originatorDto) {
        if (originatorDto == null) {
            originatorDto = new PrimaryOriginatorDTO();
            originatorDto.setType(PrimaryOriginatorType.AUTHOR);
        }
        this.originatorDto = originatorDto;
    }

    void addPrimaryOriginatorToFormFields(FormFields result) {
        Field primaryOriginator = new PrimaryOriginatorField(new LimitedLengthValidator(50), originatorDto);
        result.addField("primaryOriginator", primaryOriginator);
    }

    void setPrimaryOriginatorFromFormFields(IntelectualEntityDTO result) {
        PrimaryOriginatorDTO originator = (PrimaryOriginatorDTO) fields.getFieldByKey("primaryOriginator").getInsertedValue();
        if (originator != null && originator.getValue() != null) {
            result.setPrimaryOriginator(originator);
        }
    }

    public abstract IntelectualEntityDTO getDto();
}

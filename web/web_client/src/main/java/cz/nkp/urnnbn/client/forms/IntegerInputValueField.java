package cz.nkp.urnnbn.client.forms;

import cz.nkp.urnnbn.client.validation.Validator;

public class IntegerInputValueField extends TextInputValueField {

    public IntegerInputValueField(Validator validator, String labelText, boolean mandatory) {
        super(validator, labelText, mandatory);
    }

    public IntegerInputValueField(Validator validator, String labelText, Object value, boolean mandatory) {
        super(validator, labelText, value, mandatory);
    }

    public Integer getInsertedValue() {
        String stringValue = (String) super.getInsertedValue();
        if (stringValue == null) {
            return null;
        } else {
            return Integer.valueOf(stringValue);
        }
    }
}

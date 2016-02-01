package cz.nkp.urnnbn.client.forms;

import cz.nkp.urnnbn.client.validation.Validator;

public class DoubleInputValueField extends TextInputValueField {

    public DoubleInputValueField(Validator validator, String labelText, Object value, boolean mandatory) {
        super(validator, labelText, value, mandatory);
    }

    public DoubleInputValueField(Validator validator, String labelText, boolean mandatory) {
        super(validator, labelText, mandatory);
    }

    public Double getInsertedValue() {
        String stringValue = (String) super.getInsertedValue();
        if (stringValue == null) {
            return null;
        } else {
            return Double.valueOf(stringValue);
        }
    }
}

package cz.nkp.urnnbn.client.validation;

import com.google.gwt.i18n.shared.DateTimeFormat;

public class DateTimeValidator extends Validator {

    protected DateTimeFormat dateFormat;

    public DateTimeValidator(String format) {
        dateFormat = DateTimeFormat.getFormat(format);
    }

    @Override
    public boolean isValid(String value) {
        try {
            dateFormat.parse(value);
        } catch (IllegalArgumentException iae) {
            return false;
        }
        return true;
    }

    @Override
    public String localizedErrorMessage(String value) {
        return messages.validationInvalidDateTime();
    }

}

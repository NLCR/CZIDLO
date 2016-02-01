package cz.nkp.urnnbn.client.validation;

public class YearValidator extends RegExpValidator {

    public YearValidator() {
        super("\\d{1,4}");
    }

    @Override
    public String localizedErrorMessage(String value) {
        return messages.validationInvalidYear();
    }
}

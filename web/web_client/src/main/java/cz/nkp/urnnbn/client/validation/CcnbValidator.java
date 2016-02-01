package cz.nkp.urnnbn.client.validation;

public class CcnbValidator extends RegExpValidator {

    public CcnbValidator() {
        super("cnb\\d{9}|CNB\\d{9}");
    }

    @Override
    public String localizedErrorMessage(String value) {
        return messages.validationInvalidCcnb();
    }
}

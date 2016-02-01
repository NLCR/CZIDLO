package cz.nkp.urnnbn.client.validation;

public class EmailValidator extends RegExpValidator {

    public EmailValidator() {
        super("([\\w-]+(?:\\.[\\w-]+)*@(?:[\\w-]+\\.)+\\w{2,7})\\b");
    }

    @Override
    public String localizedErrorMessage(String value) {
        return messages.validationInvalidEmail();
    }
}

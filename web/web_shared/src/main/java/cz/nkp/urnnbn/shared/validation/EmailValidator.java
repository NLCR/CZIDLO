package cz.nkp.urnnbn.shared.validation;

public class EmailValidator extends RegExpValidator {

	public EmailValidator() {
		super("([\\w-]+(?:\\.[\\w-]+)*@(?:[\\w-]+\\.)+\\w{2,7})\\b");
	}

	@Override
	public String localizedErrorMessage(String value) {
		// TODO
		return "TODO";
		// return messages.validationInvalidEmail();
	}
}

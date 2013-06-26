package cz.nkp.urnnbn.shared.validation;

public class YearValidator extends RegExpValidator {

	public YearValidator() {
		super("\\d{1,4}");
	}

	@Override
	public String localizedErrorMessage(String value) {
		return messages.validationInvalidYear();
	}
}

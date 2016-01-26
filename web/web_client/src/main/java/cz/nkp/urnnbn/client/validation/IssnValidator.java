package cz.nkp.urnnbn.client.validation;

public class IssnValidator extends RegExpValidator {

	public IssnValidator() {
		super("\\d{4}-\\d{3}[0-9Xx]{1}");
	}

	@Override
	public String localizedErrorMessage(String value) {
		return messages.validationInvalidIssn();
	}

}

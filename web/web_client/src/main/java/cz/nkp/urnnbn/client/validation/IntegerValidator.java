package cz.nkp.urnnbn.client.validation;

public class IntegerValidator extends Validator {

	@Override
	public boolean isValid(String value) {
		try {
			Integer.valueOf(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public String localizedErrorMessage(String value) {
		return messages.validationNotInteger();
	}

}

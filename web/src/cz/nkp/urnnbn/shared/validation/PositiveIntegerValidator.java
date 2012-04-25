package cz.nkp.urnnbn.shared.validation;

public class PositiveIntegerValidator extends Validator {

	@Override
	public boolean isValid(String value) {
		try {
			Integer intValue = Integer.valueOf(value);
			return intValue > 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public String localizedErrorMessage(String value) {
		return messages.validationNotPositiveInteger();
	}
}

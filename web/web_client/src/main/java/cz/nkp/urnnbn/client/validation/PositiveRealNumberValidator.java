package cz.nkp.urnnbn.client.validation;

public class PositiveRealNumberValidator extends Validator {

	@Override
	public boolean isValid(String value) {
		try {
			Double doubleValue = Double.valueOf(value);
			return doubleValue > 0.0;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public String localizedErrorMessage(String value) {
		return messages.validationNotPositiveRealNumber();
	}

}

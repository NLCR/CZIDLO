package cz.nkp.urnnbn.shared.validation;

public class LimitedLengthValidator extends Validator {

	final int maxLength;

	public LimitedLengthValidator(int maxLength) {
		this.maxLength = maxLength;
	}

	@Override
	public boolean isValid(String value) {
		return value != null && value.length() <= maxLength;
	}

	@Override
	public String localizedErrorMessage(String value) {
		int length = value == null ? 0 : value.length();
		return messages.validationTooLong(maxLength, length - maxLength);
	}
}

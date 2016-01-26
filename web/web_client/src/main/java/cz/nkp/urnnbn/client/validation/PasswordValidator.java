package cz.nkp.urnnbn.client.validation;

public class PasswordValidator extends Validator {

	private final int minLength;
	private final int maxLength;

	public PasswordValidator(int minLength, int maxLength) {
		if (minLength <= 0 || minLength > maxLength) {
			throw new IllegalArgumentException();
		}
		this.minLength = minLength;
		this.maxLength = maxLength;
	}

	@Override
	public boolean isValid(String value) {
		return value.length() >= minLength && value.length() <= maxLength && containsNumber(value);
	}

	private boolean containsNumber(String value) {
		for (int i = 0; i < value.length(); i++) {
			if (Character.isDigit(value.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String localizedErrorMessage(String value) {
		return messages.validationInvalidPassword(minLength, maxLength);
	}

	public int getMinLength() {
		return minLength;
	}

	public int getMaxLength() {
		return maxLength;
	}
}

package cz.nkp.urnnbn.shared.validation;

public class LimitedLengthValidator extends Validator {

	final int maxLength;
	final int minLength;

	public LimitedLengthValidator(int minLength, int maxLength) {
		this.minLength = minLength;
		this.maxLength = maxLength;
	}
	
	public LimitedLengthValidator(int maxLength) {
		this(0,maxLength);
	}
		
	@Override
	public boolean isValid(String value) {
		return value != null && value.length() >= minLength && value.length() <= maxLength;
	}

	@Override
	public String localizedErrorMessage(String value) {
		int length = value == null ? 0 : value.length();
		// TODO
		return "TODO";
		// return messages.validationTooLong(length, minLength, maxLength);
	}
}

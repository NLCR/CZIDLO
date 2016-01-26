package cz.nkp.urnnbn.client.validation;

public class LimitedLengthUrlValidator extends Validator {

	private static final String[] prefices = new String[] { "http://", "https://" };
	private final int maxLength;

	public LimitedLengthUrlValidator(int maxLength) {
		this.maxLength = maxLength;
	}

	@Override
	public boolean isValid(String value) {
		return value != null && value.length() <= maxLength && hasCorrectPrefix(value);
	}

	private boolean hasCorrectPrefix(String value) {
		for (String prefix : prefices) {
			if (value.startsWith(prefix)) {
				// cannot be only prefix itself
				return value.length() > prefix.length();
			}
		}
		return false;
	}

	@Override
	public String localizedErrorMessage(String value) {
		int length = value == null ? 0 : value.length();
		int diff = length - maxLength;
		int overLength = diff > 0 ? diff : 0;
		return messages.validationNotLimitedLengthUrl(maxLength, overLength);
	}
}

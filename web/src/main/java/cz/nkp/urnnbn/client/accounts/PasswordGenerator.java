package cz.nkp.urnnbn.client.accounts;

import java.util.Random;

import cz.nkp.urnnbn.shared.validation.PasswordValidator;

public class PasswordGenerator {

	private static final String DIGITS = "0123456789";
	private static final double DIGIT_PROB = 0.12;
	private static final String VOWELS = "aeiou";
	private static final double VOWEL_PROB = 0.35;
	private static final String CONSONANTS = "bcdfghjklmnpqrstvwxz";
	private static final double UPPER_CASE_PROB = 0.2;
	private final PasswordValidator validator;
	private final Random generator = new Random();

	public PasswordGenerator(PasswordValidator validator) {
		this.validator = validator;
	}

	public String generatePassword() {
		String password = null;
		do {
			password = tryAndGenerate();
		} while (!validator.isValid(password));
		return password;
	}

	String tryAndGenerate() {
		int passLength = generatePassLength();
		StringBuilder result = new StringBuilder(passLength);
		double digitProb = DIGIT_PROB;
		double vowelProb = VOWEL_PROB;

		for (int i = 0; i < passLength; i++) {
			double category = generator.nextDouble();
			if (category < digitProb) {
				result.append(randomChar(DIGITS));
				digitProb = DIGIT_PROB * 0.3;
			} else if (category < digitProb + vowelProb) {
				result.append(randomChar(VOWELS));
				vowelProb = VOWEL_PROB * 0.4;
				digitProb = digitProb * 1.5;
			} else {
				result.append(randomChar(CONSONANTS));
				vowelProb = VOWEL_PROB * 1.2;
			}
		}
		return result.toString();
	}

	private char randomChar(String charArray) {
		int position = generator.nextInt(charArray.length());
		char result = charArray.charAt(position);
		if (generator.nextDouble() < UPPER_CASE_PROB) {
			return Character.toUpperCase(result);
		} else {
			return result;
		}
	}

	private int generatePassLength() {
		int actualSize = generator.nextInt(validator.getMaxLength() - validator.getMinLength());
		return validator.getMinLength() + actualSize;
	}
}

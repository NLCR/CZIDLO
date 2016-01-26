package cz.nkp.urnnbn.client.validation;

import com.google.gwt.regexp.shared.RegExp;

public abstract class RegExpValidator extends Validator {

	RegExp compiled;

	public RegExpValidator(String regExp) {
		this(regExp, false);
	}

	public RegExpValidator(String regExp, boolean caseSensitive) {
		if (caseSensitive) {
			compiled = RegExp.compile(regExp);
		} else {
			compiled = RegExp.compile(regExp, "i");
		}
	}

	@Override
	public final boolean isValid(String value) {
		return value == null || value.isEmpty() || compiled.test(value);
	}

	public abstract String localizedErrorMessage(String value);
}

package cz.nkp.urnnbn.shared.validation;

import com.google.gwt.regexp.shared.RegExp;

public abstract class RegExpValidator extends Validator {

	RegExp compiled;

	public RegExpValidator(String regExp) {
		compiled = RegExp.compile(regExp);
	}

	@Override
	public final boolean isValid(String value) {
		return value == null || value.isEmpty() || compiled.test(value);
	}

	public abstract String localizedErrorMessage(String value);
}

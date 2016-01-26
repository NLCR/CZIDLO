package cz.nkp.urnnbn.shared.validation;

public class IsbnValidator extends RegExpValidator {

	public IsbnValidator() {
		super(
				"(978){0,1}80\\d([0-9]|){6}\\d[0-9xX]|(978-){0,1}80-\\d([0-9]|-){6}\\d-[0-9xX]|(978\\s){0,1}80\\s\\d([0-9]|\\s){6}\\d\\s[0-9xX]|978-80\\d([0-9]|){6}\\d[0-9xX]");
	}

	@Override
	public String localizedErrorMessage(String value) {
		// TODO
		return "TODO";
		// return messages.validationInvalidIsbn();
	}
}

package cz.nkp.urnnbn.shared.validation;


public class UrnNbnPartCValidator extends RegExpValidator {

	public UrnNbnPartCValidator() {
		super("^[0-9a-z]{2,6}$");
	}

	@Override
	public String localizedErrorMessage(String value) {
		return messages.validationInvalidUrnNbnPartC();
	}

}

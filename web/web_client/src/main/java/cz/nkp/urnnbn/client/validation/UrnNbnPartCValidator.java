package cz.nkp.urnnbn.client.validation;

public class UrnNbnPartCValidator extends RegExpValidator {

	public UrnNbnPartCValidator() {
		super("\\w{6}", true);
	}

	@Override
	public String localizedErrorMessage(String value) {
		return messages.validationInvalidUrnNbnPartC();
	}

}

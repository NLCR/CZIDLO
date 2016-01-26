package cz.nkp.urnnbn.shared.validation;


public class UrnNbnPartCValidator extends RegExpValidator {

	public UrnNbnPartCValidator() {
		super("\\w{6}", true);
	}

	@Override
	public String localizedErrorMessage(String value) {
		// TODO
		return "TODO";
		// return messages.validationInvalidUrnNbnPartC();
	}

}

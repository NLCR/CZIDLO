package cz.nkp.urnnbn.shared.validation;

import com.google.gwt.core.client.GWT;

// TODO
//import cz.nkp.urnnbn.client.i18n.MessagesImpl;

public abstract class Validator {

	// TODO
	//MessagesImpl messages = GWT.create(MessagesImpl.class);

	public abstract boolean isValid(String value);

	/**
	 * 
	 * @param value
	 *            value to be validated
	 * @return String containing localized error message if value is invalid and undefined value
	 *         (often but not necessarily null)
	 */
	public abstract String localizedErrorMessage(String value);

}

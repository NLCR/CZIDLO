package cz.nkp.urnnbn.shared.validation;

import com.google.gwt.core.client.GWT;

import cz.nkp.urnnbn.client.i18n.MessagesImpl;

public abstract class Validator {

	MessagesImpl messages = GWT.create(MessagesImpl.class);

	public abstract boolean isValid(String value);

	public abstract String localizedErrorMessage(String value);

}

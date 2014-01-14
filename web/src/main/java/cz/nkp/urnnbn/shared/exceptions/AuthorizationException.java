package cz.nkp.urnnbn.shared.exceptions;

import java.io.Serializable;

public class AuthorizationException extends Exception implements Serializable {

	private static final long serialVersionUID = 4636398311412159371L;

	public AuthorizationException() {
		super();
	}

	public AuthorizationException(String msg) {
		super(msg);
	}

}

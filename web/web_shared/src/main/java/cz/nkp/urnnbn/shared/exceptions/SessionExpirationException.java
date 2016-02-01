package cz.nkp.urnnbn.shared.exceptions;

import java.io.Serializable;

public class SessionExpirationException extends Exception implements Serializable {

    private static final long serialVersionUID = 4081718306780243687L;
    public static final String MESSAGE = "SESSION EXPIRED";

    public SessionExpirationException() {
        super(MESSAGE);
    }

}

package cz.nkp.urnnbn.services.exceptions;

/**
 * Created by Martin Řehánek on 22.11.18.
 */
public class InvalidUserException extends Exception {

    public InvalidUserException(String login) {
        super("invalid user " + login);
    }
}

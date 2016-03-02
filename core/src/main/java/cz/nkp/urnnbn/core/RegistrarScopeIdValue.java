/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarScopeIdValue {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 60;
    private static char[] SPECIAL_CHARACTERS_ALLOWED = { '-', '_', '.', '~', '!', '*', '\'', '(', ')', ';', ':', '@', '&', '=', '+', '$', ',', '/',
            '?', '#', '[', ']' };

    private String value;

    private RegistrarScopeIdValue(String value) {
        this.value = value;
    }

    public static RegistrarScopeIdValue valueOf(String stringValue) {
        if (stringValue == null) {
            throw new NullPointerException();
        }
        if (stringValue.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("must be at least " + MIN_LENGTH + " characters long, actual length: " + stringValue.length());
        }
        if (stringValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("must be at most " + MAX_LENGTH + " characters long, actual length: " + stringValue.length());
        }
        for (int i = 0; i < stringValue.length(); i++) {
            char character = stringValue.charAt(i);
            if (!Character.isLetter(character) && !Character.isDigit(character)) {
                boolean isPermitted = false;
                for (int j = 0; j < SPECIAL_CHARACTERS_ALLOWED.length; j++) {
                    if (SPECIAL_CHARACTERS_ALLOWED[j] == character) {
                        isPermitted = true;
                        break;
                    }
                }
                if (!isPermitted) {
                    throw new IllegalArgumentException("character '" + character + "' at position " + i + " is not permitted");
                }
            }
        }
        return new RegistrarScopeIdValue(stringValue);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RegistrarScopeIdValue other = (RegistrarScopeIdValue) obj;
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}

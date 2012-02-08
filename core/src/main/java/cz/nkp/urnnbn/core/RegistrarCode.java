/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarCode {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 6;
    private final String value;

    public static RegistrarCode valueOf(String string) {
        if (string.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("registrat code to short, must be at least " + MIN_LENGTH + " characters long");
        }
        if (string.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("registrat code to long, must be at most " + MAX_LENGTH + " characters long");
        }
        return new RegistrarCode(string);
    }

    private RegistrarCode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

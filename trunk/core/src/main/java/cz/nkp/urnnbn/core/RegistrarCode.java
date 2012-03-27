/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarCode {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 6;
    private static final Pattern PATTERN = Pattern.compile("\\w*");
    private final String value;

    public static RegistrarCode valueOf(String string) {
        if (string.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("registrat code '" + string + "' to short, must be at least " + MIN_LENGTH + " characters long");
        }
        if (string.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("registrat code '" + string + "' to long, must be at most " + MAX_LENGTH + " characters long");
        }
        if (!containsOnlyWordCharacters(string)) {
            throw new IllegalArgumentException("registrat code '" + string + "' contains illegal character");
        }
        return new RegistrarCode(string);
    }

    private static boolean containsOnlyWordCharacters(String string) {
        Matcher matcher = PATTERN.matcher(string);
        return matcher.matches();
    }

    private RegistrarCode(String value) {
        this.value = value.toLowerCase();
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
        final RegistrarCode other = (RegistrarCode) obj;
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core;

import java.util.regex.Pattern;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarCode {

    /**
     * Must contain only small/big letters and numers. Min length is 2, max length is 6.
     */
    private static final String REGEXP = "^[a-zA-Z0-9]{2,6}$";
    private static final Pattern PATTERN = Pattern.compile(REGEXP);
    private final String value;

    public static RegistrarCode valueOf(String string) {
        if (PATTERN.matcher(string).matches()) {
            return new RegistrarCode(string);
        } else {
            throw new IllegalArgumentException(String.format("%s doesn't match regexp %s", string, REGEXP));
        }
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

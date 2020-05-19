/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core;

import java.util.regex.Pattern;

/**
 * @author Martin Řehánek
 */
public class RegistrarScopeIdValue {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 80;
    /*
     * Min length 1, max length 70, can contain small, big letters, numbers, and following characters: :?#[]@!$&'()*+,;=-._~ I.e. all reserved and
     * unreserved characters as specified inrfc3986, except for '/'. Must start and end with letter/number.
     */
    private static final String REGEXP = "^[a-zA-Z0-9]{1}[a-zA-Z0-9:\\?#\\[\\]@!\\$&'\\(\\)\\*\\+,;=\\-\\._~]{0,78}[a-zA-Z0-9]{1}$|[a-zA-Z0-9]{1}";
    private static final Pattern PATTERN = Pattern.compile(REGEXP);
    private String value;

    private RegistrarScopeIdValue(String value) {
        this.value = value;
    }

    public static RegistrarScopeIdValue valueOf(String string) {
        if (string == null) {
            throw new NullPointerException();
        }
        if (PATTERN.matcher(string).matches()) {
            return new RegistrarScopeIdValue(string);
        } else {
            throw new IllegalArgumentException(String.format("%s doesn't match regexp %s", string, REGEXP));
        }
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

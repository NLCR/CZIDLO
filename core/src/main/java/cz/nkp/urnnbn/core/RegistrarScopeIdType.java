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
public class RegistrarScopeIdType {

    /*
     * min length 2, max length 20, can contain small, big letters, numbers, characters '_', ':' and '-'. Must start and end with letter/number.
     */
    private static final String REGEXP = "^[a-zA-Z0-9]{1}[a-zA-Z0-9_:\\-]{0,18}[a-zA-Z0-9]{1}$";
    private static final Pattern PATTERN = Pattern.compile(REGEXP);
    private String value;

    private RegistrarScopeIdType(String value) {
        this.value = value;
    }

    public static RegistrarScopeIdType valueOf(String string) {
        if (PATTERN.matcher(string).matches()) {
            return new RegistrarScopeIdType(string);
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
        final RegistrarScopeIdType other = (RegistrarScopeIdType) obj;
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

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
public class Sigla {

    private static Pattern SIGLA_PATTERN = Pattern.compile("[a-zA-z]{3}[0-9]{3}");
    private final String value;

    public static Sigla valueOf(String string) {
        Matcher matcher = SIGLA_PATTERN.matcher(string);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("'" + string + "' doesn't match " + SIGLA_PATTERN);
        }
        return new Sigla(string);
    }

    private Sigla(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

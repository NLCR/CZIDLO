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

    private static Pattern urnNbnPattern = Pattern.compile("[a-zA-z]{3}[0-9]{3}");
    private final String value;

    public static Sigla valueOf(String string) {
        Matcher matcher = urnNbnPattern.matcher(string);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("sigla '" + string + "' is invalid");
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

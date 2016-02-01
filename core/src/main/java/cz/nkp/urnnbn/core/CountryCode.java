/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ISO 3166 country code that is part of urn:nbn and identifies national subspace. This singleton should be initialized only once at application
 * startup TODO: should be used in all applications instead of still hardcoded "CZ" or "cz"
 *
 * @author Martin Řehánek
 */
public class CountryCode {

    private static String code = null;

    /**
     * Initializes the national code. Should be called only once at the application startup.
     *
     * @param code
     *            ISO 3166 country code
     */
    public static void initialize(String code) {
        if (CountryCode.code != null) {
            Logger.getLogger(CountryCode.class.getName()).log(Level.WARNING,
                    "Initializing country code for the second time (was ''{0}'', now setting ''{1}'')", new Object[] { CountryCode.code, code });
        }
        CountryCode.code = code.toLowerCase();
    }

    /**
     *
     * @return ISO 3166 country code
     */
    public static String getCode() {
        return code;
    }
}

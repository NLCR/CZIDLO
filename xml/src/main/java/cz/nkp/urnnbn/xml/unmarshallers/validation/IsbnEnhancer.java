/*
 * Copyright (C) 2012 Martin Řehánek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.nkp.urnnbn.xml.unmarshallers.validation;

/**
 * TODO: transformations from isbn-10 to ISBN-13 so that everything is stored
 * http://andrewu.co.uk/tools/isbn/
 *
 * @author Martin Řehánek
 */
public class IsbnEnhancer implements ElementContentEnhancer {

    private static final char[] SEPARATORS = {'-', ' '};
    private static final String[] PREFICIES = {"ISBN ", "ISBN: ", "ISBN:"};
    private static final String STANDARD_PREFIX = "ISBN ";
    private static final String REGEXP = "(978){0,1}80\\d([0-9]|){6}\\d[0-9xX]|(978-){0,1}80-\\d([0-9]|-){6}\\d-[0-9xX]|(978\\s){0,1}80\\s\\d([0-9]|\\s){6}\\d\\s[0-9xX]|978-80\\d([0-9]|){6}\\d[0-9xX]";

    @Override
    public String toEnhancedValueOrNull(String originalContent) {
        if (originalContent == null || originalContent.isEmpty()) {
            return null;
        }
        String isbn = removeSeparators(removePrefix(originalContent.toUpperCase()));
        if (matchesRegexp(isbn) && hasCorrectChecksum(isbn)) {
            //return STANDARD_PREFIX + isbn;
            return isbn;
        } else {
            return null;
        }
    }

    private String removePrefix(String original) {
        for (String prefix : PREFICIES) {
            if (original.startsWith(prefix)) {
                return original.substring(prefix.length());
            }
        }
        return original;
    }

    private String removeSeparators(String original) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < original.length(); i++) {
            char character = original.charAt(i);
            if (!isSeparator(character)) {
                result.append(character);
            }
        }
        return result.toString();
    }

    private boolean isSeparator(char character) {
        for (char separator : SEPARATORS) {
            if (character == separator) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesRegexp(String original) {
        return original.matches(REGEXP);
    }

    private boolean hasCorrectChecksum(String original) {
        //TODO: implement
        return true;
    }
}

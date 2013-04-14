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
 *
 * @author Martin Řehánek
 */
public class LimitedLengthEnhancer implements ElementContentEnhancer {

    public static final String SUFFIX = " ...";
    private final int minLength;
    private final int maxLength;

    public LimitedLengthEnhancer(int minLength, int maxLength) {
        this.minLength = minLength;
        this.maxLength = maxLength;
        if (minLength < 1) {
            throw new IllegalArgumentException("minLength (" + minLength + ") must be positive");
        }
        if (maxLength < 1) {
            throw new IllegalArgumentException("maxLength (" + maxLength + ") must be positive");
        }
        if (minLength > maxLength) {
            throw new IllegalArgumentException("minLength (" + minLength + ") > maxLength (" + maxLength + ")");
        }
        if (maxLength <= SUFFIX.length()) {
            throw new IllegalArgumentException("maxLength (" + maxLength + ") too short, must be at least " + SUFFIX.length());
        }
    }

    public LimitedLengthEnhancer(int maxLength) {
        this(1, maxLength);
    }

    @Override
    public String toEnhancedValueOrNull(String original) {
        if (original == null || original.isEmpty() || containsOnlyWhiteSpaces(original)) {
            return null;
        }
        if (original.length() < minLength) {
            return null;
        } else if (original.length() > maxLength) {
            return shorten(original);
        } else { //length OK
            return original;
        }
    }

    private boolean containsOnlyWhiteSpaces(String original) {
        for (int i = 0; i < original.length(); i++) {
            if (!Character.isWhitespace(original.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private String shorten(String original) {
        int stringLength = maxLength - SUFFIX.length();
        return original.substring(0, stringLength) + SUFFIX;
    }
}

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
package cz.nkp.urnnbn.xml.apiv4.unmarshallers.validation;

import java.util.logging.Logger;

/**
 * Accepts issn in forms like "1234-5678", "1234-567x", "1234-567X", "ISSN: 1234-5678", "ISSN 1234-5678", "ISSN:1234-5678", "issn: 1234-5678", "issn
 * 1234-5678", "issn:1234-5678" and returns issn in standard value "1234-567X", i. e. without any prefix and with potential x in upper cas.
 * 
 * @author Martin Řehánek
 */
public class IssnEnhancer implements ElementContentEnhancer {

    private static final Logger logger = Logger.getLogger(IssnEnhancer.class.getName());

    private static final String[] PREFICIES = { "ISSN ", "ISSN: ", "ISSN:" };
    private static final String REGEXP = "\\d{4}-\\d{3}[0-9Xx]{1}";

    @Override
    public String toEnhancedValueOrNull(String originalContent) {
        if (originalContent == null || originalContent.isEmpty()) {
            return null;
        }
        String issn = removePrefix(originalContent.toUpperCase());
        if (matchesRegexp(issn) && hasCorrectChecksum(issn)) {
            return issn;
        } else {
            logger.warning("invalid ISSN '" + originalContent + "', dropping");
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

    private boolean matchesRegexp(String original) {
        return original.matches(REGEXP);
    }

    private boolean hasCorrectChecksum(String original) {
        int sum = 0;
        int weight = 9;
        for (int i = 0; i < 9; i++) {
            char character = original.charAt(i);
            if (character == '-') {
                continue;
            } else {
                String valueStr = (character == 'x' || character == 'X') ? "10" : String.valueOf(character);
                weight--;
                int valueAtPosition = Integer.parseInt(valueStr);
                sum += weight * valueAtPosition;
            }
        }
        return sum % 11 == 0;
    }
}

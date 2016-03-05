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
 * 
 * @author Martin Řehánek
 */
public class CcnbEnhancer implements ElementContentEnhancer {

    private static final Logger logger = Logger.getLogger(CcnbEnhancer.class.getName());

    private static final String STANDARD_PREFIX = "cnb";
    private static final String[] PREFICIES = { "cnb", "čnb" };
    private static final String REGEXP = "[0-9]{9}";

    @Override
    public String toEnhancedValueOrNull(String originalContent) {
        if (originalContent == null || originalContent.isEmpty()) {
            return null;
        }
        String ccnb = removePrefix(originalContent.toLowerCase());
        if (ccnb.matches(REGEXP)) {
            return STANDARD_PREFIX + ccnb;
        } else {
            logger.warning("invalid cCNB '" + originalContent + "', dropping");
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
}

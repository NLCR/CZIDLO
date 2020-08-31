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
package cz.nkp.urnnbn.xml.apiv6.unmarshallers.validation;

import java.util.logging.Logger;

/**
 * 
 * @author Martin Řehánek
 */
public class UrlValidator implements ElementContentEnhancer {

    private static final Logger logger = Logger.getLogger(UrlValidator.class.getName());
    private static final String[] PREFICIES = { "HTTP://", "HTTPS://", "http://", "https://" };
    private final int maxLength;

    public UrlValidator(int maxLength) {
        this.maxLength = maxLength;
        if (maxLength < 1) {
            throw new IllegalArgumentException("maxLength (" + maxLength + ") must be positive");
        }
    }

    @Override
    public String toEnhancedValueOrNull(String originalContent) {
        if (originalContent == null || originalContent.isEmpty()) {
            return null;
        }
        if (originalContent.length() > maxLength) {
            logger.warning("to long URL '" + originalContent + "', dropping");
            return null;
        }
        for (String prefix : PREFICIES) {
            if (originalContent.startsWith(prefix)) {
                return originalContent;
            }
        }
        logger.warning("invalid URL '" + originalContent + "', dropping");
        return null;
    }
}

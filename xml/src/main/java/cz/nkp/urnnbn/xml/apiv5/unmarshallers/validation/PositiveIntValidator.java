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
package cz.nkp.urnnbn.xml.apiv5.unmarshallers.validation;

import java.util.logging.Logger;

/**
 * 
 * @author Martin Řehánek
 */
public class PositiveIntValidator implements ElementContentEnhancer {

    private static final Logger logger = Logger.getLogger(PositiveIntValidator.class.getName());

    @Override
    public String toEnhancedValueOrNull(String originalContent) {
        if (originalContent == null || originalContent.isEmpty()) {
            return null;
        }
        try {
            int intValue = Integer.parseInt(originalContent);
            if (intValue > 0) {
                return originalContent;
            } else {
                logger.warning("not positive number '" + originalContent + "', dropping");
                return null;
            }
        } catch (NumberFormatException e) {
            logger.warning("not a number '" + originalContent + "', dropping");
            return null;
        }
    }
}

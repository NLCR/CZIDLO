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

import java.util.logging.Logger;

import org.apache.commons.validator.routines.ISBNValidator;

/**
 * 
 * @author Martin Řehánek
 */
public class IsbnEnhancer implements ElementContentEnhancer {

	private static final Logger logger = Logger.getLogger(IsbnEnhancer.class.getName());

	private static final char[] SEPARATORS = { '-', ' ' };
	private static final String[] PREFICIES = { "ISBN ", "ISBN: ", "ISBN:" };
	private ISBNValidator validator = new ISBNValidator(true);

	@Override
	public String toEnhancedValueOrNull(String originalContent) {
		if (originalContent == null || originalContent.isEmpty()) {
			return null;
		} else {
			String withoutPrefixAndSepatators = removeSeparators(removePrefix(originalContent.toUpperCase()));
			if (validator.isValid(withoutPrefixAndSepatators)) {
				return withoutPrefixAndSepatators;
			} else {
				logger.warning("invalid ISBN '" + originalContent + "', dropping");
				return null;
			}
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
}

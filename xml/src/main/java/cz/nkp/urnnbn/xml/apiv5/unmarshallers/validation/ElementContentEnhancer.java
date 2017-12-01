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

/**
 *
 * @author Martin Řehánek
 */
public interface ElementContentEnhancer {

    /**
     * Tries to enhance value from original content of element so that it is valid. For instance if there is length limitation the enhancer cuts the
     * original String to demanded length. If there is no way to construct valid value, null is returned.
     *
     * @param originalContent
     *            original content of element
     * @return enhanced value or null if there cannot be obtained valid enhanced value from iriginal content.
     */
    String toEnhancedValueOrNull(String originalContent);
}

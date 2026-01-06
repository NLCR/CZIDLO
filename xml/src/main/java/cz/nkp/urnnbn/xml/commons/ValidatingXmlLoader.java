/*
 * Copyright (C) 2013 Martin Řehánek
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
package cz.nkp.urnnbn.xml.commons;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;

import org.xml.sax.XMLReader;

/**
 *
 * @author Martin Řehánek
 */
public abstract class ValidatingXmlLoader {

    protected final XMLReader reader;

    public ValidatingXmlLoader(XMLReader reader) {
        this.reader = reader;
    }

    protected final Builder builder() {
        return new Builder(reader);
    }

    public Document loadDocument(InputStream xmlStream) throws ParsingException, IOException {
        return builder().build(xmlStream);
    }

    public Document loadDocument(String xmlString) throws ParsingException, IOException {
        xmlString = normalizeWhitespaces(xmlString);
        return builder().build(xmlString, null);
    }

    /**
     * Normalizes various weird whitespace characters in the input XML string.
     * Keeps ASCII space, tab, newline, and carriage return as-is.
     * Converts other Unicode whitespace characters to normal space (U+0020).
     * Removes common zero-width formatting characters that may interfere with XML parsing.
     *
     * @param xmlString
     * @return
     */
    private String normalizeWhitespaces(String xmlString) {
        if (xmlString == null || xmlString.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder(xmlString.length());

        for (int i = 0; i < xmlString.length(); ) {
            int cp = xmlString.codePointAt(i);
            i += Character.charCount(cp);

            // Keep ASCII space + tab/newlines exactly as-is
            if (cp == 0x20 || cp == 0x09 || cp == 0x0A || cp == 0x0D) {
                sb.appendCodePoint(cp);
                continue;
            }

            // Drop common "zero-width" / formatting chars that can appear from copy&paste
            // (They are not whitespace per se, but frequently break validation or comparisons.)
            if (cp == 0x200B  // ZERO WIDTH SPACE
                    || cp == 0x200C // ZERO WIDTH NON-JOINER
                    || cp == 0x200D // ZERO WIDTH JOINER
                    || cp == 0x2060 // WORD JOINER
                    || cp == 0xFEFF // ZERO WIDTH NO-BREAK SPACE (BOM)
            ) {
                // skip
                continue;
            }

            // Convert "weird whitespace" to normal space.
            // This catches NBSP (U+00A0), NNBSP (U+202F), Ideographic Space (U+3000),
            // and many other Unicode spaces.
            if (Character.isWhitespace(cp) || Character.getType(cp) == Character.SPACE_SEPARATOR) {
                sb.append(' ');
                continue;
            }

            // Otherwise keep the original character
            sb.appendCodePoint(cp);
        }

        return sb.toString();
    }

    public Document loadDocument(File xmlFile) throws ParsingException, IOException {
        return builder().build(xmlFile);
    }
}

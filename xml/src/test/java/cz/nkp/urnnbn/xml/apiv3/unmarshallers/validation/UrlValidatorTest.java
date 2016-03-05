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
package cz.nkp.urnnbn.xml.apiv3.unmarshallers.validation;

import junit.framework.TestCase;

/**
 *
 * @author Martin Řehánek
 */
public class UrlValidatorTest extends TestCase {

    public UrlValidatorTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testConstructor() {
        try {
            new UrlValidator(-1);
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new UrlValidator(0);
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    public void testEnhancePreficies() {
        ElementContentEnhancer enhancer = new UrlValidator(10);
        assertNull(enhancer.toEnhancedValueOrNull(null));
        assertNull(enhancer.toEnhancedValueOrNull(""));
        assertNull(enhancer.toEnhancedValueOrNull("ttp://7890"));
        assertNull(enhancer.toEnhancedValueOrNull("httpss://0"));
        assertNull(enhancer.toEnhancedValueOrNull("ftp://7890"));

        assertNull(enhancer.toEnhancedValueOrNull("http://8901"));
        assertNull(enhancer.toEnhancedValueOrNull("https://901"));
        assertNull(enhancer.toEnhancedValueOrNull("HTTP://8901"));
        assertNull(enhancer.toEnhancedValueOrNull("HTTPS://901"));
    }

    public void testEnhanceLength() {
        ElementContentEnhancer enhancer = new UrlValidator(10);
        assertEquals("http://890", enhancer.toEnhancedValueOrNull("http://890"));
        assertEquals("HTTP://890", enhancer.toEnhancedValueOrNull("HTTP://890"));
        assertEquals("https://90", enhancer.toEnhancedValueOrNull("https://90"));
        assertEquals("HTTPS://90", enhancer.toEnhancedValueOrNull("HTTPS://90"));
    }
}

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
public class IssnEnhancerTest extends TestCase {

    public IssnEnhancerTest(String testName) {
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

    public void testEnhanceObviouslyInvalid() {
        ElementContentEnhancer enhancer = new IssnEnhancer();
        assertNull(enhancer.toEnhancedValueOrNull(null));
        assertNull(enhancer.toEnhancedValueOrNull(""));
    }

    public void testEnhance() {
        ElementContentEnhancer enhancer = new IssnEnhancer();
        assertEquals("1214-4029", enhancer.toEnhancedValueOrNull("1214-4029"));
        assertNull(enhancer.toEnhancedValueOrNull("1214-402X"));
        assertNull(enhancer.toEnhancedValueOrNull("1214-4028"));
        assertNull(enhancer.toEnhancedValueOrNull("1214-4027"));
        assertNull(enhancer.toEnhancedValueOrNull("1214-4026"));
        assertNull(enhancer.toEnhancedValueOrNull("1214-4025"));
        assertNull(enhancer.toEnhancedValueOrNull("1214-4024"));
        assertNull(enhancer.toEnhancedValueOrNull("1214-4023"));
        assertNull(enhancer.toEnhancedValueOrNull("1214-4022"));
        assertNull(enhancer.toEnhancedValueOrNull("1214-4021"));
        assertNull(enhancer.toEnhancedValueOrNull("1214-4020"));
    }

    public void testEnhanceXNormalization() {
        ElementContentEnhancer enhancer = new IssnEnhancer();
        assertEquals("2434-561X", enhancer.toEnhancedValueOrNull("2434-561x"));
        assertEquals("2434-561X", enhancer.toEnhancedValueOrNull("2434-561X"));
        assertEquals("2434-561X", enhancer.toEnhancedValueOrNull("ISSN 2434-561x"));
        assertEquals("2434-561X", enhancer.toEnhancedValueOrNull("ISSN:2434-561x"));
        assertEquals("2434-561X", enhancer.toEnhancedValueOrNull("ISSN: 2434-561x"));
        assertEquals("2434-561X", enhancer.toEnhancedValueOrNull("issn 2434-561x"));
        assertEquals("2434-561X", enhancer.toEnhancedValueOrNull("issn:2434-561x"));
        assertEquals("2434-561X", enhancer.toEnhancedValueOrNull("issn: 2434-561x"));
    }
}

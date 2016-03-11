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
public class LimitedLengthEnhancerTest extends TestCase {

    public LimitedLengthEnhancerTest(String testName) {
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

    public void testSingleParamConstructor() {
        try {
            new LimitedLengthEnhancer(-1);
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new LimitedLengthEnhancer(0);
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new LimitedLengthEnhancer(LimitedLengthEnhancer.SUFFIX.length());
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
        new LimitedLengthEnhancer(LimitedLengthEnhancer.SUFFIX.length() + 1);
    }

    public void testTwoParamConstructor() {
        int maxLengthCorrect = LimitedLengthEnhancer.SUFFIX.length() + 1;
        try {
            new LimitedLengthEnhancer(-1, maxLengthCorrect);
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new LimitedLengthEnhancer(0, maxLengthCorrect);
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            new LimitedLengthEnhancer(maxLengthCorrect + 1, maxLengthCorrect);
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
        new LimitedLengthEnhancer(maxLengthCorrect, maxLengthCorrect);
    }

    public void testEnhanceMaxLength() {
        ElementContentEnhancer enhancer = new LimitedLengthEnhancer(5);
        assertNull(enhancer.toEnhancedValueOrNull(null));
        assertNull(enhancer.toEnhancedValueOrNull(""));
        assertEquals(enhancer.toEnhancedValueOrNull("1"), "1");
        assertEquals(enhancer.toEnhancedValueOrNull("12"), "12");
        assertEquals(enhancer.toEnhancedValueOrNull("123"), "123");
        assertEquals(enhancer.toEnhancedValueOrNull("1234"), "1234");
        assertEquals(enhancer.toEnhancedValueOrNull("12345"), "12345");
        assertEquals(enhancer.toEnhancedValueOrNull("123456"), "1 ...");
        assertEquals(enhancer.toEnhancedValueOrNull("1234567"), "1 ...");
        assertEquals(enhancer.toEnhancedValueOrNull("123456789"), "1 ...");
    }

    public void testEnhanceMinMaxLength() {
        ElementContentEnhancer enhancer = new LimitedLengthEnhancer(3, 5);
        assertNull(enhancer.toEnhancedValueOrNull(null));
        assertNull(enhancer.toEnhancedValueOrNull(""));
        assertNull(enhancer.toEnhancedValueOrNull("1"));
        assertNull(enhancer.toEnhancedValueOrNull("12"));
        assertEquals(enhancer.toEnhancedValueOrNull("123"), "123");
        assertEquals(enhancer.toEnhancedValueOrNull("1234"), "1234");
        assertEquals(enhancer.toEnhancedValueOrNull("12345"), "12345");
        assertEquals(enhancer.toEnhancedValueOrNull("123456"), "1 ...");
        assertEquals(enhancer.toEnhancedValueOrNull("1234567"), "1 ...");
        assertEquals(enhancer.toEnhancedValueOrNull("123456789"), "1 ...");
    }

    public void testEnhanceGivenLength() {
        ElementContentEnhancer enhancer = new LimitedLengthEnhancer(5, 5);
        assertNull(enhancer.toEnhancedValueOrNull(null));
        assertNull(enhancer.toEnhancedValueOrNull(""));
        assertNull(enhancer.toEnhancedValueOrNull("1"));
        assertNull(enhancer.toEnhancedValueOrNull("12"));
        assertNull(enhancer.toEnhancedValueOrNull("123"));
        assertNull(enhancer.toEnhancedValueOrNull("1234"));
        assertEquals(enhancer.toEnhancedValueOrNull("12345"), "12345");
        assertEquals(enhancer.toEnhancedValueOrNull("123456"), "1 ...");
        assertEquals(enhancer.toEnhancedValueOrNull("1234567"), "1 ...");
        assertEquals(enhancer.toEnhancedValueOrNull("12345678"), "1 ...");
    }
}

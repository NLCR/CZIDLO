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

import junit.framework.TestCase;

/**
 *
 * @author Martin Řehánek
 */
public class IsbnEnhancerTest extends TestCase {

    public IsbnEnhancerTest(String testName) {
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
        ElementContentEnhancer enhancer = new IsbnEnhancer();
        assertEquals("8090119964", enhancer.toEnhancedValueOrNull("8090119964"));
        assertEquals("9788090119963", enhancer.toEnhancedValueOrNull("9788090119963"));
        assertEquals("807178463X", enhancer.toEnhancedValueOrNull("807178463x"));
        assertEquals("807178463X", enhancer.toEnhancedValueOrNull("807178463X"));
        assertEquals("978807178463X", enhancer.toEnhancedValueOrNull("978807178463x"));
        assertEquals("978807178463X", enhancer.toEnhancedValueOrNull("978807178463X"));
    }

    public void testEnhancePreficies() {
        ElementContentEnhancer enhancer = new IsbnEnhancer();
        assertEquals("978807178463X", enhancer.toEnhancedValueOrNull("ISBN 978-80-7178-463-x"));
        assertEquals("978807178463X", enhancer.toEnhancedValueOrNull("isbn 978-80-7178-463-x"));
        assertEquals("978807178463X", enhancer.toEnhancedValueOrNull("ISBN: 978-80-7178-463-x"));
        assertEquals("978807178463X", enhancer.toEnhancedValueOrNull("isbn: 978-80-7178-463-x"));
        assertEquals("978807178463X", enhancer.toEnhancedValueOrNull("ISBN:978-80-7178-463-x"));
        assertEquals("978807178463X", enhancer.toEnhancedValueOrNull("isbn:978-80-7178-463-x"));
    }

    public void testEnhanceSeparators() {
        ElementContentEnhancer enhancer = new IsbnEnhancer();
        assertEquals("8090119964", enhancer.toEnhancedValueOrNull("8090119964"));
        assertEquals("8090119964", enhancer.toEnhancedValueOrNull("80 9 01199 64"));
        assertEquals("9788090119963", enhancer.toEnhancedValueOrNull("978 80 901199 6 3"));
        assertEquals("807178463X", enhancer.toEnhancedValueOrNull("80 7178 463 x"));
        assertEquals("807178463X", enhancer.toEnhancedValueOrNull("80 7178 463 X"));
        assertEquals("978807178463X", enhancer.toEnhancedValueOrNull("978 80 7178 463 x"));
        assertEquals("978807178463X", enhancer.toEnhancedValueOrNull("978 80 7178 463 X"));

        assertEquals("8090119964", enhancer.toEnhancedValueOrNull("80-9-01199-64"));
        assertEquals("9788090119963", enhancer.toEnhancedValueOrNull("978-80-901199-6-3"));
        assertEquals("807178463X", enhancer.toEnhancedValueOrNull("80-7178-463-x"));
        assertEquals("807178463X", enhancer.toEnhancedValueOrNull("80-7178-463-X"));
        assertEquals("978807178463X", enhancer.toEnhancedValueOrNull("978-80-7178-463-x"));
        assertEquals("978807178463X", enhancer.toEnhancedValueOrNull("978-80-7178-463-X"));

        assertEquals("9788090119963", enhancer.toEnhancedValueOrNull("978-8090119963"));
        assertEquals("978807178463X", enhancer.toEnhancedValueOrNull("978-807178463x"));
        assertEquals("978807178463X", enhancer.toEnhancedValueOrNull("978-807178463X"));
    }
}

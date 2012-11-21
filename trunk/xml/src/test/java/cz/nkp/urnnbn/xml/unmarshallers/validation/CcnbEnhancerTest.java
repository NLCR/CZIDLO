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
public class CcnbEnhancerTest extends TestCase {

    public CcnbEnhancerTest(String testName) {
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
        ElementContentEnhancer enhancer = new CcnbEnhancer();
        assertNull(enhancer.toEnhancedValueOrNull(null));
        assertNull(enhancer.toEnhancedValueOrNull(""));
    }

    public void testEnhance() {
        ElementContentEnhancer enhancer = new CcnbEnhancer();
        assertNull(enhancer.toEnhancedValueOrNull("12345678"));
        assertNull(enhancer.toEnhancedValueOrNull("ccnb12345678"));
        assertEquals("cnb123456789", enhancer.toEnhancedValueOrNull("123456789"));
        assertEquals("cnb123456789", enhancer.toEnhancedValueOrNull("cnb123456789"));
        assertEquals("cnb123456789", enhancer.toEnhancedValueOrNull("čnb123456789"));
        assertEquals("cnb123456789", enhancer.toEnhancedValueOrNull("CNB123456789"));
        assertEquals("cnb123456789", enhancer.toEnhancedValueOrNull("ČNB123456789"));
    }
}

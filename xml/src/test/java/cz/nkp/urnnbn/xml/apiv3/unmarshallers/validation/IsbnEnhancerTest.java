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
        assertEquals("807178463X", enhancer.toEnhancedValueOrNull("807178463X"));
        assertEquals("807178463X", enhancer.toEnhancedValueOrNull("807178463x"));
        assertEquals("1402894627", enhancer.toEnhancedValueOrNull("1-4028-9462-7"));
    }

    public void testEnhancePreficies() {
        ElementContentEnhancer enhancer = new IsbnEnhancer();

        // ISBN
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("ISBN 3-7705-4739-X"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("ISBN 3 7705 4739 X"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("ISBN 377054739X"));

        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("ISBN 3-7705-4739-x"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("ISBN 3 7705 4739 x"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("ISBN 377054739x"));

        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("ISBN 978-3-7705-4739-5"));
        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("ISBN 978 3 7705 4739 5"));
        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("ISBN 9783770547395"));

        // ISBN:
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("ISBN:3-7705-4739-X"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("ISBN:3 7705 4739 X"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("ISBN:377054739X"));

        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("ISBN:3-7705-4739-x"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("ISBN:3 7705 4739 x"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("ISBN:377054739x"));

        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("ISBN:978-3-7705-4739-5"));
        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("ISBN:978 3 7705 4739 5"));
        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("ISBN:9783770547395"));

        // ISBN:_
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("ISBN: 3-7705-4739-X"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("ISBN: 3 7705 4739 X"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("ISBN: 377054739X"));

        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("ISBN: 3-7705-4739-x"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("ISBN: 3 7705 4739 x"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("ISBN: 377054739x"));

        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("ISBN: 978-3-7705-4739-5"));
        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("ISBN: 978 3 7705 4739 5"));
        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("ISBN: 9783770547395"));

        // isbn
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("isbn 3-7705-4739-X"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("isbn 3 7705 4739 X"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("isbn 377054739X"));

        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("isbn 3-7705-4739-x"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("isbn 3 7705 4739 x"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("isbn 377054739x"));

        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("isbn 978-3-7705-4739-5"));
        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("isbn 978 3 7705 4739 5"));
        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("isbn 9783770547395"));

        // isbn:
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("isbn:3-7705-4739-X"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("isbn:3 7705 4739 X"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("isbn:377054739X"));

        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("isbn:3-7705-4739-x"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("isbn:3 7705 4739 x"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("isbn:377054739x"));

        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("isbn:978-3-7705-4739-5"));
        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("isbn:978 3 7705 4739 5"));
        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("isbn:9783770547395"));

        // isbn:_
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("isbn: 3-7705-4739-X"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("isbn: 3 7705 4739 X"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("isbn: 377054739X"));

        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("isbn: 3-7705-4739-x"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("isbn: 3 7705 4739 x"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("isbn: 377054739x"));

        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("isbn: 978-3-7705-4739-5"));
        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("isbn: 978 3 7705 4739 5"));
        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("isbn: 9783770547395"));
    }

    public void testEnhanceSeparators() {
        ElementContentEnhancer enhancer = new IsbnEnhancer();

        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("3-7705-4739-X"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("3 7705 4739 X"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("377054739X"));

        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("3-7705-4739-x"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("3 7705 4739 x"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("377054739x"));

        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("978-3-7705-4739-5"));
        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("978 3 7705 4739 5"));
        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("9783770547395"));
    }

    public void testEnhanceOther() {
        ElementContentEnhancer enhancer = new IsbnEnhancer();
        assertEquals("8020009809", enhancer.toEnhancedValueOrNull("8020009809"));
        assertEquals("8020009809", enhancer.toEnhancedValueOrNull("80-200-0980-9"));

        assertEquals("9780199283408", enhancer.toEnhancedValueOrNull("978-0-19-928340-8"));
        assertEquals("0199283400", enhancer.toEnhancedValueOrNull("0-19-928340-0"));
        assertEquals("0199283400", enhancer.toEnhancedValueOrNull("0199283400"));

        assertEquals("9780813215457", enhancer.toEnhancedValueOrNull("978-0-8132-1545-7"));
        assertEquals("9780813215457", enhancer.toEnhancedValueOrNull("9780813215457"));
        assertEquals("0813215455", enhancer.toEnhancedValueOrNull("0-8132-1545-5"));
        assertEquals("0813215455", enhancer.toEnhancedValueOrNull("0813215455"));

        assertEquals("9780813215457", enhancer.toEnhancedValueOrNull("978-0-8132-1545-7"));
        assertEquals("9780813215457", enhancer.toEnhancedValueOrNull("9780813215457"));
        assertEquals("0813215455", enhancer.toEnhancedValueOrNull("0-8132-1545-5"));
        assertEquals("0813215455", enhancer.toEnhancedValueOrNull("0813215455"));

        assertEquals("9004177868", enhancer.toEnhancedValueOrNull("90-04-17786-8"));
        assertEquals("9004177868", enhancer.toEnhancedValueOrNull("9004177868"));
        assertEquals("9789004177864", enhancer.toEnhancedValueOrNull("978-90-04-17786-4"));
        assertEquals("9789004177864", enhancer.toEnhancedValueOrNull("9789004177864"));

        assertEquals("3895349577", enhancer.toEnhancedValueOrNull("3-89534-957-7"));
        assertEquals("3895349577", enhancer.toEnhancedValueOrNull("3895349577"));
        assertEquals("9783895349577", enhancer.toEnhancedValueOrNull("978-3-89534-957-7"));
        assertEquals("9783895349577", enhancer.toEnhancedValueOrNull("9783895349577"));

        assertEquals("3937233261", enhancer.toEnhancedValueOrNull("3-937233-26-1"));
        assertEquals("3937233261", enhancer.toEnhancedValueOrNull("3937233261"));
        assertEquals("9783937233260", enhancer.toEnhancedValueOrNull("978-3-937233-26-0"));
        assertEquals("9783937233260", enhancer.toEnhancedValueOrNull("9783937233260"));

        assertEquals("0631181857", enhancer.toEnhancedValueOrNull("0-631-18185-7"));
        assertEquals("0631181857", enhancer.toEnhancedValueOrNull("0631181857"));
        assertEquals("9780631181859", enhancer.toEnhancedValueOrNull("978-0-631-18185-9"));
        assertEquals("9780631181859", enhancer.toEnhancedValueOrNull("9780631181859"));

        assertEquals("8371773951", enhancer.toEnhancedValueOrNull("83-7177-395-1"));
        assertEquals("8371773951", enhancer.toEnhancedValueOrNull("8371773951"));
        assertEquals("9788371773952", enhancer.toEnhancedValueOrNull("978-83-7177-395-2"));
        assertEquals("9788371773952", enhancer.toEnhancedValueOrNull("9788371773952"));

        assertEquals("0007126646", enhancer.toEnhancedValueOrNull("0-00-712664-6"));
        assertEquals("0007126646", enhancer.toEnhancedValueOrNull("0007126646"));
        assertEquals("9780007126644", enhancer.toEnhancedValueOrNull("978-0-00-712664-4"));
        assertEquals("9780007126644", enhancer.toEnhancedValueOrNull("9780007126644"));

        assertEquals("0316015040", enhancer.toEnhancedValueOrNull("0-316-01504-0"));
        assertEquals("0316015040", enhancer.toEnhancedValueOrNull("0316015040"));
        assertEquals("9780316015042", enhancer.toEnhancedValueOrNull("978-0-316-01504-2"));
        assertEquals("9780316015042", enhancer.toEnhancedValueOrNull("9780316015042"));

        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("3-7705-4739-X"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("3-7705-4739-x"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("377054739X"));
        assertEquals("377054739X", enhancer.toEnhancedValueOrNull("377054739x"));
        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("978-3-7705-4739-5"));
        assertEquals("9783770547395", enhancer.toEnhancedValueOrNull("9783770547395"));
    }

    /**
     * Examples from http://andrewu.co.uk/tools/isbn/.
     */
    public void testEnhanceInvalid() {
        ElementContentEnhancer enhancer = new IsbnEnhancer();
        assertNotNull(enhancer.toEnhancedValueOrNull("123456789X"));
        assertNotNull(enhancer.toEnhancedValueOrNull("ISBN 978-0-306-40615-7"));
        assertNotNull(enhancer.toEnhancedValueOrNull("0123-4567-89"));
        assertNotNull(enhancer.toEnhancedValueOrNull("ISBN 184353066X"));
        assertNotNull(enhancer.toEnhancedValueOrNull("978-0-356-42615-0"));
        assertNotNull(enhancer.toEnhancedValueOrNull("ISBN 0 571 08989 5"));
        assertNotNull(enhancer.toEnhancedValueOrNull("isbn 0-671657-15-1"));
    }

    /**
     * https://github.com/NLCR/CZIDLO/issues/57
     */
    public void testIssue57() {
        ElementContentEnhancer enhancer = new IsbnEnhancer();
        assertEquals("9780123747204", enhancer.toEnhancedValueOrNull("978-0-12-374720-4"));
        assertEquals("0521472997", enhancer.toEnhancedValueOrNull("0 521 47299 7"));
    }
}

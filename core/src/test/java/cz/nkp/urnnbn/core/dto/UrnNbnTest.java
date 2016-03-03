/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

import junit.framework.TestCase;
import cz.nkp.urnnbn.core.CountryCode;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnTest extends TestCase {

    public UrnNbnTest(String testName) {
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

    public void testValueOf_ok() {
        CountryCode.initialize("cz");
        UrnNbn urn = UrnNbn.valueOf("urn:nbn:cz:aba001-123456");
        assertEquals(urn.getRegistrarCode().toString(), "aba001");
        assertEquals(urn.getDocumentCode(), "123456");
        UrnNbn urn2 = UrnNbn.valueOf("urn:nbn:cz:ab-123456");
        assertEquals(urn2.getRegistrarCode().toString(), "ab");
        assertEquals(urn2.getDocumentCode(), "123456");

        UrnNbn urn21 = UrnNbn.valueOf("urn:nbn:cz:a1-123456");
        assertEquals(urn21.getRegistrarCode().toString(), "a1");
        assertEquals(urn21.getDocumentCode(), "123456");

        UrnNbn urn3 = UrnNbn.valueOf("urn:nbn:cz:aba-123456");
        assertEquals(urn3.getRegistrarCode().toString(), "aba");
        assertEquals(urn3.getDocumentCode(), "123456");

        UrnNbn urn31 = UrnNbn.valueOf("urn:nbn:cz:ab1-123456");
        assertEquals(urn31.getRegistrarCode().toString(), "ab1");
        assertEquals(urn31.getDocumentCode(), "123456");

        UrnNbn urn4 = UrnNbn.valueOf("urn:nbn:cz:aba0-123456");
        assertEquals(urn4.getRegistrarCode().toString(), "aba0");
        assertEquals(urn4.getDocumentCode(), "123456");

        UrnNbn urn5 = UrnNbn.valueOf("urn:nbn:cz:aba00-123456");
        assertEquals(urn5.getRegistrarCode().toString(), "aba00");
        assertEquals(urn5.getDocumentCode(), "123456");
    }

    public void testValueOf_ok_inUpperCase() {
        CountryCode.initialize("cz");
        UrnNbn urn = UrnNbn.valueOf("URN:NBN:CZ:ABA001-123456");
        assertEquals(urn.getRegistrarCode().toString(), "aba001");
        assertEquals(urn.getDocumentCode(), "123456");
    }

    public void testValueOf_ok_alphNumDocumentCode() {
        CountryCode.initialize("cz");
        UrnNbn.valueOf("URN:NBN:CZ:ABA001-123A56");
        UrnNbn.valueOf("URN:NBN:CZ:ABA001-aaaBBB");
        UrnNbn.valueOf("URN:NBN:CZ:ABA001-aaa123");
    }

    public void testValueOf_incorrectPrefix() {
        CountryCode.initialize("cz");
        try {
            UrnNbn.valueOf("ur:nbn:cz:aba001-123456");
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            UrnNbn.valueOf("urnnbn:cz:aba001-123456");
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    public void testValueOf_incorrectCountryCode() {
        CountryCode.initialize("cz");
        try {
            UrnNbn.valueOf("urn:nbn:czz:aba001-123456");
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            UrnNbn.valueOf("urn:nbn:c:aba001-123456");
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    public void testValueOf_incorrectRegistrarCode() {
        CountryCode.initialize("cz");
        try {
            UrnNbn.valueOf("urn:nbn:cz:a-123456");
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            UrnNbn.valueOf("urn:nbn:cz:1-123456");
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            UrnNbn.valueOf("urn:nbn:cz:abcd001-123456");
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            UrnNbn.valueOf("urn:nbn:cz:aba0001-123456");
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    public void testValueOf_incorrectDocumentCode() {
        CountryCode.initialize("cz");
        try {
            UrnNbn.valueOf("urn:nbn:cz:aba001-1234567");
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            UrnNbn.valueOf("urn:nbn:cz:aba001-12345");
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    public void testValueOf_incorrectSeparator() {
        CountryCode.initialize("cz");
        try {
            UrnNbn.valueOf("urn-nbn:cz:aba001-123456");
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            UrnNbn.valueOf("urn:nbn-cz:aba001-123456");
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            UrnNbn.valueOf("urn:nbn:cz:aba001:123456");
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
    }
}

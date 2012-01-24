/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.dto;

import cz.nkp.urnnbn.core.dto.UrnNbn;
import junit.framework.TestCase;

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
        UrnNbn urn = UrnNbn.valueOf("urn:nbn:cz:aba001-123456");
        assertEquals(urn.getRegistrarCode(), "aba001");
        assertEquals(urn.getDocumentCode(), "123456");
    }

    public void testValueOf_ok_inUpperCase() {
        UrnNbn urn = UrnNbn.valueOf("URN:NBN:CZ:ABA001-123456");
        assertEquals(urn.getRegistrarCode(), "aba001");
        assertEquals(urn.getDocumentCode(), "123456");
    }

    public void testValueOf_ok_alphNumDocumentCode() {
        UrnNbn.valueOf("URN:NBN:CZ:ABA001-123A56");
        UrnNbn.valueOf("URN:NBN:CZ:ABA001-aaaBBB");
        UrnNbn.valueOf("URN:NBN:CZ:ABA001-aaa123");
    }

    public void testValueOf_incorrectPrefix() {
        try {
            UrnNbn.valueOf("ur:nbn:cz:aba001-123456");
            fail();
        } catch (IllegalArgumentException e) {
            //ok
        }
        try {
            UrnNbn.valueOf("urnnbn:cz:aba001-123456");
            fail();
        } catch (IllegalArgumentException e) {
            //ok
        }
    }

    public void testValueOf_incorrectCountryCode() {
        try {
            UrnNbn.valueOf("urn:nbn:czz:aba001-123456");
            fail();
        } catch (IllegalArgumentException e) {
            //ok
        }
        try {
            UrnNbn.valueOf("urn:nbn:c:aba001-123456");
            fail();
        } catch (IllegalArgumentException e) {
            //ok
        }
    }

    public void testValueOf_incorrectRegistrarCode() {
        try {
            UrnNbn.valueOf("urn:nbn:cz:ab001-123456");
            fail();
        } catch (IllegalArgumentException e) {
            //ok
        }
        try {
            UrnNbn.valueOf("urn:nbn:cz:abcd001-123456");
            fail();
        } catch (IllegalArgumentException e) {
            //ok
        }
        try {
            UrnNbn.valueOf("urn:nbn:cz:aba01-123456");
            fail();
        } catch (IllegalArgumentException e) {
            //ok
        }
        try {
            UrnNbn.valueOf("urn:nbn:cz:aba0001-123456");
            fail();
        } catch (IllegalArgumentException e) {
            //ok
        }
    }

    public void testValueOf_incorrectDocumentCode() {
        try {
            UrnNbn.valueOf("urn:nbn:cz:aba001-1234567");
            fail();
        } catch (IllegalArgumentException e) {
            //ok
        }
        try {
            UrnNbn.valueOf("urn:nbn:cz:aba001-12345");
            fail();
        } catch (IllegalArgumentException e) {
            //ok
        }
    }

    public void testValueOf_incorrectSeparator() {
        try {
            UrnNbn.valueOf("urn-nbn:cz:aba001-123456");
            fail();
        } catch (IllegalArgumentException e) {
            //ok
        }
        try {
            UrnNbn.valueOf("urn:nbn-cz:aba001-123456");
            fail();
        } catch (IllegalArgumentException e) {
            //ok
        }
        try {
            UrnNbn.valueOf("urn:nbn:cz:aba001:123456");
            fail();
        } catch (IllegalArgumentException e) {
            //ok
        }
    }
}

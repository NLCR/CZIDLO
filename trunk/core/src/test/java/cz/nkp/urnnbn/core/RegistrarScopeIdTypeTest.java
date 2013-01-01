/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core;

import junit.framework.TestCase;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarScopeIdTypeTest extends TestCase {

    public RegistrarScopeIdTypeTest(String testName) {
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

    /**
     * Test of valueOf method, of class RegistrarScopeIdType.
     */
    public void testValueOf() {
        try {
            RegistrarScopeIdType.valueOf(null);
            fail();
        } catch (NullPointerException e) {
            //null
        }
        try {
            RegistrarScopeIdType.valueOf("a");
            fail();
        } catch (IllegalArgumentException e) {
            //too short
        }
        try {
            RegistrarScopeIdType.valueOf("incredibly_long_identifier_type");
            fail();
        } catch (IllegalArgumentException e) {
            //too long
        }
        try {
            RegistrarScopeIdType.valueOf("incorrect+char");
            fail();
        } catch (IllegalArgumentException e) {
            //contains illegal character
        }
    }

    /**
     * Test of toString method, of class RegistrarScopeIdType.
     */
    public void testToString() {
        String idType = "abc_DEF-012:";
        assertEquals(idType, RegistrarScopeIdType.valueOf(idType).toString());
    }
}

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
public class RegistrarCodeTest extends TestCase {

    public RegistrarCodeTest(String testName) {
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
     * Test of valueOf method, of class RegistrarCode.
     */
    public void testValueOf() {
        RegistrarCode.valueOf("ab");
        RegistrarCode.valueOf("12");
        RegistrarCode.valueOf("a1");
        RegistrarCode.valueOf("aba");
        RegistrarCode.valueOf("aba0");
        RegistrarCode.valueOf("aba00");
        RegistrarCode.valueOf("aba001");
        RegistrarCode.valueOf("001aaa");
        try {
            RegistrarCode.valueOf("a");
            fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            RegistrarCode.valueOf("aaba001");
            fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            RegistrarCode.valueOf("aaaba001");
            fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            RegistrarCode.valueOf("aba-01");
            fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            RegistrarCode.valueOf("ába001");
            fail();
        } catch (IllegalArgumentException e) {
        }
    }
}

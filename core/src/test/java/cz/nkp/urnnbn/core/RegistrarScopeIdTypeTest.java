/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core;

import java.util.logging.Logger;

import junit.framework.TestCase;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarScopeIdTypeTest extends TestCase {

    private static final Logger LOGGER = Logger.getLogger(RegistrarScopeIdTypeTest.class.getName());

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

    public void testValueOfNull() {
        try {
            RegistrarScopeIdType.valueOf(null);
            fail();
        } catch (NullPointerException e) {
            LOGGER.info(e.getMessage());
        }
    }

    public void testValueOfEmpty() {
        try {
            RegistrarScopeIdType.valueOf("");
            fail();
        } catch (IllegalArgumentException e) {
            LOGGER.info(e.getMessage());
        }
    }

    public void testValueOfToShort() {
        try {
            RegistrarScopeIdType.valueOf("X");
            fail();
        } catch (IllegalArgumentException e) {
            LOGGER.info(e.getMessage());
        }
    }

    public void testValueOfToLong() {
        try {
            RegistrarScopeIdType.valueOf("0123456789x0123456789");
            fail();
        } catch (IllegalArgumentException e) {
            LOGGER.info(e.getMessage());
        }
    }

    public void testValueOfOk() {
        RegistrarScopeIdType.valueOf("123");
        RegistrarScopeIdType.valueOf("abc");
        RegistrarScopeIdType.valueOf("ABC");
        RegistrarScopeIdType.valueOf("1aA");
    }

    public void testValueOfSpecialChars() {
        char[] chars = new char[] { '_', ':', '-' };
        for (char c : chars) {
            // contains special character
            RegistrarScopeIdType.valueOf("x" + c + "x");

            // starts with special character
            try {
                String str = "" + c + "x";
                RegistrarScopeIdType.valueOf(str);
                fail(str);
            } catch (IllegalArgumentException e) {
                LOGGER.info(e.getMessage());
            }

            // ends with special character
            try {
                String str = "x" + c;
                RegistrarScopeIdType.valueOf(str);
                fail(str);
            } catch (IllegalArgumentException e) {
                LOGGER.info(e.getMessage());
            }
        }
    }

    public void testValueOfInvalidSpecialChars() {
        char[] invalidChars = new char[] { '/', '?', '#', '[', ']', '@', '!', '$', '&', '\'', '(', ')', '*', '+', ',', ';', '=', '.', '~' };
        for (char c : invalidChars) {
            // starts with special character
            try {
                String str = "" + c + "x";
                RegistrarScopeIdType.valueOf(str);
                fail(str);
            } catch (IllegalArgumentException e) {
                LOGGER.info(e.getMessage());
            }

            // ends with special character
            try {
                String str = "x" + c;
                RegistrarScopeIdType.valueOf(str);
                fail(str);
            } catch (IllegalArgumentException e) {
                LOGGER.info(e.getMessage());
            }

            // contains special character
            try {
                String str = "x" + c + "x";
                RegistrarScopeIdType.valueOf(str);
                fail(str);
            } catch (IllegalArgumentException e) {
                LOGGER.info(e.getMessage());
            }
        }
    }

    public void testCaseSensitive() {
        String type = "abc_DEF-012";
        assertEquals(type, RegistrarScopeIdType.valueOf(type).toString());
    }
}

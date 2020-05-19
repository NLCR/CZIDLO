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
public class RegistrarScopeIdValueTest extends TestCase {

    private static final Logger LOGGER = Logger.getLogger(RegistrarScopeIdValueTest.class.getName());

    public RegistrarScopeIdValueTest(String testName) {
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
            RegistrarScopeIdValue.valueOf(null);
            fail();
        } catch (NullPointerException e) {
            LOGGER.info(e.getMessage());
        }
    }

    public void testValueOfEmpty() {
        try {
            RegistrarScopeIdValue.valueOf("");
            fail();
        } catch (IllegalArgumentException e) {
            LOGGER.info(e.getMessage());
        }
    }

    public void testValueOfTooLong() {
        try {
            RegistrarScopeIdValue.valueOf("aaaaaaaaa1aaaaaaaaa2aaaaaaaaa3aaaaaaaaa4aaaaaaaaa5aaaaaaaaa6aaaaaaaaa7aaaaaaaaa8X");
            fail();
        } catch (IllegalArgumentException e) {
            LOGGER.info(e.getMessage());
        }
    }

    public void testValueOfOk() {
        RegistrarScopeIdValue.valueOf("1");
        RegistrarScopeIdValue.valueOf("a");
        RegistrarScopeIdValue.valueOf("A");
        RegistrarScopeIdValue.valueOf("123");
        RegistrarScopeIdValue.valueOf("abc");
        RegistrarScopeIdValue.valueOf("ABC");
        RegistrarScopeIdValue.valueOf("1aA");
        RegistrarScopeIdValue.valueOf("aaaaaaaaa1aaaaaaaaa2aaaaaaaaa3aaaaaaaaa4aaaaaaaaa5aaaaaaaaa6aaaaaaaaa7aaaaaaaaa8");
    }

    public void testValueOfSpecialChars() {
        char[] validChars = new char[] { ':', '?', '#', '[', ']', '@', '!', '$', '&', '\'', '(', ')', '*', '+', ',', ';', '=', '-', '.', '_', '~' };
        for (char c : validChars) {
            // contains special character
            RegistrarScopeIdValue.valueOf("x" + c + "x");

            // starts with special character
            try {
                String str = "" + c + "x";
                RegistrarScopeIdValue.valueOf(str);
                fail(str);
            } catch (IllegalArgumentException e) {
                LOGGER.info(e.getMessage());
            }

            // ends with special character
            try {
                String str = "x" + c;
                RegistrarScopeIdValue.valueOf(str);
                fail(str);
            } catch (IllegalArgumentException e) {
                // null
                LOGGER.info(e.getMessage());
            }
        }
    }

    public void testValueOfInvalidSpecialChars() {
        char[] invalidChars = new char[] { '/' };
        for (char c : invalidChars) {
            // starts with special character
            try {
                String str = "" + c + "x";
                RegistrarScopeIdValue.valueOf(str);
                fail(str);
            } catch (IllegalArgumentException e) {
                LOGGER.info(e.getMessage());
            }

            // ends with special character
            try {
                String str = "x" + c;
                RegistrarScopeIdValue.valueOf(str);
                fail(str);
            } catch (IllegalArgumentException e) {
                LOGGER.info(e.getMessage());
            }

            // contains special character
            try {
                String str = "x" + c + "x";
                RegistrarScopeIdValue.valueOf(str);
                fail(str);
            } catch (IllegalArgumentException e) {
                LOGGER.info(e.getMessage());
            }
        }
    }

    public void testCaseSensitive() {
        String value = "abc_DEF-012";
        assertEquals(value, RegistrarScopeIdValue.valueOf(value).toString());
    }
}

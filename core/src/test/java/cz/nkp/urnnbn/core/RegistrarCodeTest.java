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
public class RegistrarCodeTest extends TestCase {

    private static final Logger LOGGER = Logger.getLogger(RegistrarCodeTest.class.getName());

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

    public void testValueOfNull() {
        try {
            RegistrarCode.valueOf(null);
            fail();
        } catch (NullPointerException e) {
            // null
            LOGGER.info(e.getMessage());
        }
    }

    public void testValueOfEmpty() {
        try {
            RegistrarCode.valueOf("");
            fail();
        } catch (IllegalArgumentException e) {
            // null
            LOGGER.info(e.getMessage());
        }
    }

    public void testValueOfToShort() {
        try {
            RegistrarCode.valueOf("X");
            fail();
        } catch (IllegalArgumentException e) {
            // null
            LOGGER.info(e.getMessage());
        }
    }

    public void testValueOfToLong() {
        try {
            RegistrarCode.valueOf("123456x");
            fail();
        } catch (IllegalArgumentException e) {
            // null
            LOGGER.info(e.getMessage());
        }
    }

    public void testValueOfOk() {
        RegistrarCode.valueOf("12");
        RegistrarCode.valueOf("123456");
        RegistrarCode.valueOf("ab");
        RegistrarCode.valueOf("abcdef");
        RegistrarCode.valueOf("ABC");
        RegistrarCode.valueOf("ABCDEF");
        RegistrarCode.valueOf("12abAB");
    }

    public void testValueOfInvalidChar() {
        char[] invalidChars = new char[] { '-', '_', '.', '~', '!', '*', '\'', '(', ')', ';', ':', '@', '&', '=', '+', '$', ',', '/', '?', '#', '[',
                ']' };
        for (char c : invalidChars) {
            // starts with special character
            try {
                String str = "" + c + "x";
                RegistrarCode.valueOf(str);
                fail(str);
            } catch (IllegalArgumentException e) {
                LOGGER.info(e.getMessage());
            }

            // ends with special character
            try {
                String str = "x" + c;
                RegistrarCode.valueOf(str);
                fail(str);
            } catch (IllegalArgumentException e) {
                LOGGER.info(e.getMessage());
            }

            // contains special character
            try {
                String str = "x" + c + "x";
                RegistrarCode.valueOf(str);
                fail(str);
            } catch (IllegalArgumentException e) {
                LOGGER.info(e.getMessage());
            }
        }
    }

    public void testCaseInsensitive() {
        String code = "abCD01";
        assertEquals(code.toLowerCase(), RegistrarCode.valueOf(code).toString().toLowerCase());
    }

    public void testInternalRepresentationLowCase() {
        String code = "abCD01";
        assertEquals(code.toLowerCase(), RegistrarCode.valueOf(code).toString());
    }

}

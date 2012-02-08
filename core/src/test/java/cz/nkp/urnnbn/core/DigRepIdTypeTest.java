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
public class DigRepIdTypeTest extends TestCase {

    public DigRepIdTypeTest(String testName) {
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
     * Test of valueOf method, of class DigDocIdType.
     */
    public void testValueOf() {
        try {
            DigDocIdType.valueOf(null);
            fail();
        } catch (NullPointerException e) {
            //null
        }
        try {
            DigDocIdType.valueOf("a");
            fail();
        } catch (IllegalArgumentException e) {
            //too short
        }
        try {
            DigDocIdType.valueOf("incredibly_long_identifier_type");
            fail();
        } catch (IllegalArgumentException e) {
            //too long
        }
        try {
            DigDocIdType.valueOf("something+else");
            fail();
        } catch (IllegalArgumentException e) {
            //contains illegal character
        }
    }

    /**
     * Test of toString method, of class DigDocIdType.
     */
    public void testToString() {
        DigDocIdType idType = DigDocIdType.valueOf("my_id_Type1");
        assertEquals("my_id_Type1", idType.toString());
    }
}

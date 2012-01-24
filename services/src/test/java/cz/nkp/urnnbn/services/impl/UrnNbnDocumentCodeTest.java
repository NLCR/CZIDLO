/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import java.math.BigInteger;
import junit.framework.TestCase;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnDocumentCodeTest extends TestCase {

    public UrnNbnDocumentCodeTest(String testName) {
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

    public void testValueOf() {
        UrnNbnDocumentCode.valueOf("000000");
        UrnNbnDocumentCode.valueOf("aaaaaa");
        UrnNbnDocumentCode.valueOf("AAAAAA");
    }

    public void testValueOf_illegalLength() {
        try {
            //too long
            UrnNbnDocumentCode.valueOf("0a0a0a0");
            fail();
        } catch (RuntimeException e) {
            //ok
        }
        try {
            //too short
            UrnNbnDocumentCode.valueOf("0a0a0");
            fail();
        } catch (RuntimeException e) {
            //ok
        }
    }

    public void testValueOf_illegalCharacter() {
        try {
            UrnNbnDocumentCode.valueOf("0a0:0a0");
            fail();
        } catch (RuntimeException e) {
            //ok
        }
        try {
            UrnNbnDocumentCode.valueOf("0a0a0+0");
            fail();
        } catch (RuntimeException e) {
            //ok
        }
        try {
            UrnNbnDocumentCode.valueOf("_a0a0d0");
            fail();
        } catch (RuntimeException e) {
            //ok
        }
    }

    public void testNumerals() {
        assertEquals(UrnNbnDocumentCode.NUMERALS.length, 36);
    }

    public void testInternalValue() {
        assertEquals(UrnNbnDocumentCode.valueOf("000000").internalValue(), BigInteger.valueOf(0));
        assertEquals(UrnNbnDocumentCode.valueOf("000001").internalValue(), BigInteger.valueOf(1));
        assertEquals(UrnNbnDocumentCode.valueOf("00000z").internalValue(), BigInteger.valueOf(35));
        assertEquals(UrnNbnDocumentCode.valueOf("zzzzzz").internalValue(), UrnNbnDocumentCode.INTERNAL_VALUE_MAX);
        assertEquals(UrnNbnDocumentCode.valueOf("zzzzzy").internalValue(), UrnNbnDocumentCode.INTERNAL_VALUE_MAX.subtract(BigInteger.ONE));
    }

    public void testStringValue() {
        assertEquals(UrnNbnDocumentCode.valueOf("0a0a0a").toString(), "0a0a0a");
        assertEquals(UrnNbnDocumentCode.valueOf("0A0A0A").toString(), "0a0a0a");
        assertEquals(UrnNbnDocumentCode.valueOf("ZA0H0A").toString(), "za0h0a");
    }

    /**
     * Test of getNext method, of class UrnNbnDocumentCode.
     */
    public void testGetNext() {
        //+0
        assertEquals(UrnNbnDocumentCode.valueOf("0a0a0a").getNext(0).toString(),
                UrnNbnDocumentCode.valueOf("0a0a0a").toString());
        //+0
        assertEquals(UrnNbnDocumentCode.valueOf("zzzzzz").getNext(0).toString(),
                UrnNbnDocumentCode.valueOf("zzzzzz").toString());
        //+3
        assertEquals(UrnNbnDocumentCode.valueOf("0a0a0a").getNext(3).toString(),
                UrnNbnDocumentCode.valueOf("0a0a0d").toString());
        //+36
        assertEquals(UrnNbnDocumentCode.valueOf("0a0a0a").getNext(36).toString(),
                UrnNbnDocumentCode.valueOf("0a0a1a").toString());
        //+37
        assertEquals(UrnNbnDocumentCode.valueOf("0a0a0a").getNext(37).toString(),
                UrnNbnDocumentCode.valueOf("0a0a1b").toString());
        //+36^2
        assertEquals(UrnNbnDocumentCode.valueOf("0a0a0a").getNext(36 * 36).toString(),
                UrnNbnDocumentCode.valueOf("0a0b0a").toString());
        //+1 with overflow
        assertEquals(UrnNbnDocumentCode.valueOf("zzzzzz").getNext(1).toString(),
                UrnNbnDocumentCode.valueOf("000000").toString());
        //+10 with overflow
        assertEquals(UrnNbnDocumentCode.valueOf("zzzzzz").getNext(10).toString(),
                UrnNbnDocumentCode.valueOf("000009").toString());
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnDocumentCode {

    public static final BigInteger INTERNAL_VALUE_MIN = new BigInteger("0");
    public static final BigInteger INTERNAL_VALUE_MAX = new BigInteger("2176782335");
    public static char[] NUMERALS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
    // there are 36 numerals on 6 positions. I. e. maximal value is 36^6 - 1
    private static final BigInteger CODE_MOD_BASE = new BigInteger("2176782336");
    private static final int CODE_LENGTH = 6;
    private static final BigInteger RADIX = BigInteger.valueOf(36);
    private static final Map<Character, BigInteger> valueMap = initValueMap();
    private final BigInteger internalValue;

    private static Map<Character, BigInteger> initValueMap() {
        Map<Character, BigInteger> result = new HashMap<Character, BigInteger>(NUMERALS.length);
        for (int i = 0; i < NUMERALS.length; i++) {
            Character numeral = NUMERALS[i];
            result.put(numeral, BigInteger.valueOf(i));
        }
        return result;
    }

    private UrnNbnDocumentCode(BigInteger internalValue) {
        this.internalValue = internalValue;
    }

    public static UrnNbnDocumentCode valueOf(String stringValue) {
        if (stringValue.length() != CODE_LENGTH) {
            throw new IllegalArgumentException("'" + stringValue + "' does not have length " + CODE_LENGTH);
        }
        return toBigInt(stringValue.toLowerCase());
    }

    private static UrnNbnDocumentCode toBigInt(String stringValue) {
        BigInteger result = BigInteger.valueOf(0);
        for (int order = 0; order < CODE_LENGTH; order++) {
            int positionInString = CODE_LENGTH - order - 1;
            char digit = stringValue.charAt(positionInString);
            checkCharIsAllowed(digit);
            BigInteger digitValue = valueMap.get(digit); // 0-35
            BigInteger valueAtPosition = digitValue.multiply(RADIX.pow(order));
            result = result.add(valueAtPosition);
        }
        return new UrnNbnDocumentCode(result);
    }

    private static void checkCharIsAllowed(char digit) {
        if (!Character.isDigit(digit) && !Character.isLetter(digit)) {
            throw new IllegalArgumentException("illegal character " + digit);
        }
    }

    public String toString() {
        int[] valuesAtPositions = toValuesAtPositions(internalValue, RADIX, CODE_LENGTH);
        // printIntArray(valuesAtPositions);
        StringBuilder result = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < valuesAtPositions.length; i++) {
            result.append(NUMERALS[valuesAtPositions[i]]);
        }
        return result.toString();
    }

    /**
     * For number 123 would be produced this result (for radix=10 and resultSize=6): [0,0,0,1,2,3]
     * 
     * @param num
     * @param radix
     * @param resultSize
     * @return
     */
    private int[] toValuesAtPositions(BigInteger num, BigInteger radix, int resultSize) {
        int[] values = new int[resultSize];
        for (int order = values.length - 1; order >= 0; order--) {
            int positionInArray = values.length - order - 1;
            BigInteger devidedBy = radix.pow(order);
            BigInteger[] divideAndRemainder = num.divideAndRemainder(devidedBy);
            values[positionInArray] = divideAndRemainder[0].intValue();
            num = divideAndRemainder[1];
        }
        return values;
    }

    /**
     * only for testing
     * 
     * @return
     */
    public BigInteger internalValue() {
        return internalValue;
    }

    /**
     * definition of urnNbnDocumentCode plus number
     * 
     * @param step
     * @return
     */
    public UrnNbnDocumentCode getNext(int step) {
        BigInteger plusStep = addStep(step);
        return new UrnNbnDocumentCode(plusStep);
    }

    private BigInteger addStep(int step) {
        BigInteger afterStep = BigInteger.valueOf(step).add(internalValue);
        return afterStep.mod(CODE_MOD_BASE);
    }

    private void printIntArray(int[] array) {
        System.out.print("[");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]);
            if (i == array.length - 1) {
                System.out.print("]");
            } else {
                System.out.print(",");
            }
        }
    }
}

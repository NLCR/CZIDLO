package cz.nkp.urnnbn.client;


import com.google.gwt.regexp.shared.RegExp;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Martin Řehánek on 23.11.17.
 */
public class CzechCharComparator implements Comparator<Character> {

    //gwt
    private final RegExp PATTERN_WHITE_SPACE = RegExp.compile("\\s");
    //java
    //private final Pattern PATTERN_WHITE_SPACE = Pattern.compile("\\s");
    private final Map<Character, Integer> LETTER_POSITION_MAP = buildLetterPositionMap(
            "AaÁáBbCcČčDdĎďEeÉéĚěFfGgHhIiÍíJjKkLlMmNnŇňOoÓóPpQqRrŘřSsŠšTtŤťUuÚúŮůVvWwXxYyÝýZzŽž".toCharArray());

    private Map<Character, Integer> buildLetterPositionMap(char[] chars) {
        Map<Character, Integer> map = new HashMap<>(chars.length);
        for (int i = 0; i < chars.length; i++) {
            map.put(chars[i], i);
        }
        return map;
    }

    @Override
    public int compare(Character first, Character second) {
        //spaces, numbers, known letters, rest (other characters, letters)
        if (isWhitespace(first) && isWhitespace(second)) { // space, space
            return 0;
        } else if (isWhitespace(first)) { // space,any
            return -1;
        } else if (isWhitespace(second)) { // any,space
            return 1;
        } else if (isDigit(first) && isDigit(first)) { // digit,digit
            return first.compareTo(second);
        } else if (isDigit(first)) { // digit,letter
            return -1;
        } else if (isDigit(second)) {// letter,digit
            return 1;
        } else if (isKnownLetter(first) && isKnownLetter(second)) { // known_letter,known_letter
            return compareKnownLetters(first, second);
        } else if (isKnownLetter(first)) {// known_letter,unknown_letter
            return -1;
        } else if (isKnownLetter(second)) {// unknown_letter,known_letter
            return 1;
        } else { // unknown_letter,unknown_letter
            return first.compareTo(second);
        }
    }

    private boolean isWhitespace(Character c) {
        //gtw
        return PATTERN_WHITE_SPACE.test(String.valueOf(c));
        //java
        //return PATTERN_WHITE_SPACE.matcher(String.valueOf(c)).matches();
    }

    private boolean isDigit(Character first) {
        return Character.isDigit(first);
    }

    private boolean isKnownLetter(char c) {
        return LETTER_POSITION_MAP.containsKey(c);
    }

    private int compareKnownLetters(char first, char second) {
        int firstPosition = LETTER_POSITION_MAP.get(first);
        int secondPosition = LETTER_POSITION_MAP.get(second);
        return firstPosition - secondPosition;
    }

}

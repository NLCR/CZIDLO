package cz.nkp.urnnbn.client;

import java.util.Comparator;

/**
 * Created by Martin Řehánek on 23.11.17.
 */
public class CzechStringComparator implements Comparator<String> {

    private final Comparator<Character> charComparator = new CzechCharComparator();

    @Override
    public int compare(String first, String second) {
        if (isNullOrEmpty(first) && isNullOrEmpty(second)) {
            return 0;
        } else if (isNullOrEmpty(first)) {
            return -1;
        } else if (isNullOrEmpty(second)) {
            return 1;
        } else {
            int minLength = Math.min(first.length(), second.length());
            for (int i = 0; i < minLength; i++) {
                int charCompare = charComparator.compare(first.charAt(i), second.charAt(i));
                if (charCompare != 0) { //different on current char
                    return charCompare;
                }

            }
            //"office" < "officer"
            return first.length() - second.length();
        }
    }

    private boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

}

package cz.nkp.urnnbn.shared.dto.ie;

public class Utils {

    public static String normalizeIsbn(String isbn) {
        if (isbn == null || isbn.isEmpty()) {
            return "";
        } else {
            return isbn.replaceAll("-", "").replaceAll(" ", "");
        }
    }
}

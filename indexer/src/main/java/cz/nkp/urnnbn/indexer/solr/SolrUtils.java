package cz.nkp.urnnbn.indexer.solr;

/**
 * Created by Martin Řehánek on 31.1.18.
 */
public class SolrUtils {

    public static String escapeSolrSpecialChars(String string) {
        string = string.replaceAll("\\+", "\\\\+");
        string = string.replaceAll("-", "\\\\-");
        string = string.replaceAll("&&", "\\\\&&");
        string = string.replaceAll("\\|\\|", "\\\\||");
        string = string.replaceAll("!", "\\\\!");
        string = string.replaceAll("\\(", "\\\\(");
        string = string.replaceAll("\\)", "\\\\)");
        string = string.replaceAll("\\{", "\\\\{");
        string = string.replaceAll("\\}", "\\\\}");
        string = string.replaceAll("\\[", "\\\\[");
        string = string.replaceAll("\\]", "\\\\]");
        string = string.replaceAll("\\^", "\\\\^");
        string = string.replaceAll("\\\"", "\\\\\"");
        string = string.replaceAll("\\~", "\\\\~");
        string = string.replaceAll("\\*", "\\\\*");
        string = string.replaceAll("\\?", "\\\\?");
        string = string.replaceAll("\\:", "\\\\:");
        string = string.replaceAll("\\/", "\\\\/");
        return string;
    }

}

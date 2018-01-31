package cz.nkp.urnnbn.czidlo_indexer;

import cz.nkp.urnnbn.solr_indexer.SolrUtils;
import junit.framework.TestCase;

/**
 * Created by Martin Řehánek on 31.1.18.
 */
public class SolrUtilsTest extends TestCase {

    public void testSolrSpecialCharEscaping() {
        assertEquals("first\\+second\\+third", SolrUtils.escapeSolrSpecialChars("first+second+third"));
        assertEquals("first\\-second\\-third", SolrUtils.escapeSolrSpecialChars("first-second-third"));
        assertEquals("first\\&&second\\&&third", SolrUtils.escapeSolrSpecialChars("first&&second&&third"));
        assertEquals("first\\||second\\||third", SolrUtils.escapeSolrSpecialChars("first||second||third"));
        assertEquals("first\\!second\\!third", SolrUtils.escapeSolrSpecialChars("first!second!third"));
        assertEquals("first\\(second\\(third", SolrUtils.escapeSolrSpecialChars("first(second(third"));
        assertEquals("first\\)second\\)third", SolrUtils.escapeSolrSpecialChars("first)second)third"));
        assertEquals("first\\{second\\{third", SolrUtils.escapeSolrSpecialChars("first{second{third"));
        assertEquals("first\\}second\\}third", SolrUtils.escapeSolrSpecialChars("first}second}third"));
        assertEquals("first\\[second\\[third", SolrUtils.escapeSolrSpecialChars("first[second[third"));
        assertEquals("first\\]second\\]third", SolrUtils.escapeSolrSpecialChars("first]second]third"));
        assertEquals("first\\^second\\^third", SolrUtils.escapeSolrSpecialChars("first^second^third"));
        assertEquals("first\\\"second\\\"third", SolrUtils.escapeSolrSpecialChars("first\"second\"third"));
        assertEquals("first\\~second\\~third", SolrUtils.escapeSolrSpecialChars("first~second~third"));
        assertEquals("first\\*second\\*third", SolrUtils.escapeSolrSpecialChars("first*second*third"));
        assertEquals("first\\?second\\?third", SolrUtils.escapeSolrSpecialChars("first?second?third"));
        assertEquals("first\\:second\\:third", SolrUtils.escapeSolrSpecialChars("first:second:third"));
        assertEquals("first\\/second\\/third", SolrUtils.escapeSolrSpecialChars("first/second/third"));
    }

}

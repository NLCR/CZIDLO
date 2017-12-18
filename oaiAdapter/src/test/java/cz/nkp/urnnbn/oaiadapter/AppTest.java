package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.oaiadapter.cli.App;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    public void testOaiAdapter() {
        //CountryCode.initialize("CZ");
        String configFile = "src/test/resources/mzk.properties";
        //String configFile = "src/test/resources/nkp.properties";
        App.main(new String[]{configFile});
    }

}

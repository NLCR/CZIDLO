package cz.nkp.urnnbn.api.v4;

import java.util.logging.Logger;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This class is used for debugging Utils, ApiV3Tests methods, etc.
 * 
 * @author martin
 *
 */
public class Debug extends ApiV4Tests {

    private static final Logger LOGGER = Logger.getLogger(Debug.class.getName());

    @BeforeClass
    public void beforeClass() {
        init();
    }

    @Test
    public void test() {
        String urnNbn = getRandomFreeUrnNbnOrNull(REGISTRAR);
        LOGGER.info(urnNbn);
    }
}

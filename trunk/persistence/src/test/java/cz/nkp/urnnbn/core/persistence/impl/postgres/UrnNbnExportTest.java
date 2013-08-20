package cz.nkp.urnnbn.core.persistence.impl.postgres;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;

import cz.nkp.urnnbn.core.dto.UrnNbnExport;

public class UrnNbnExportTest extends AbstractDaoTest {
	
    public UrnNbnExportTest(String testName) {
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
    
    public void testSelectAll() throws Exception {
    	List<UrnNbnExport> result = super.urnDao.selectByCriteria(DateTime.now().minusDays(30), DateTime.now(), Arrays.asList("mzk", "nkp"), null, "MONOGRAPH", true, true, true, true);
    }
    
    private void dump(List<UrnNbnExport> results) {
    	for (UrnNbnExport result : results) {
    		System.out.println(result.getReserved() + " " + result.getModified() + " " + result.getUrn() + " " + result.isIssnAssigned());
    	}
    }
}

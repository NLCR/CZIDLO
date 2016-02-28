package cz.nkp.urnnbn.api.v3;

import java.util.logging.Logger;

import org.testng.annotations.BeforeClass;

/**
 * Tests for GET /api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}/digitalInstances
 *
 */
public class GetDigitalInstancesResolvedByRsIdTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(GetDigitalInstancesResolvedByRsIdTests.class.getName());

    private String urnNbn;

    @BeforeClass
    public void beforeClass() {
        init();
        urnNbn = registerUrnNbn(REGISTRAR, USER);
    }

    private String buildUrl(RsId idForResolvation, String idType, String idValue) {
        return buildResolvationPath(idForResolvation) + "/digitalInstances";
    }

    // TODO: implement tests

}

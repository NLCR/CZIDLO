package cz.nkp.urnnbn.api.v4;

import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.UrnNbn;

public abstract class AbstractUrnNbnResource extends V4Resource {

    public abstract String getUrnNbnXmlRecord(String urnNbnString);

    protected final UrnNbnWithStatus getUrnNbnWithStatus(String urnNbnString) {
        UrnNbn urnParsed = Parser.parseUrn(urnNbnString);
        return dataAccessService().urnByRegistrarCodeAndDocumentCode(urnParsed.getRegistrarCode(), urnParsed.getDocumentCode(), true);
    }

    protected final UrnNbnWithStatus getUrnNbnWithStatus(UrnNbn urn) {
        return dataAccessService().urnByRegistrarCodeAndDocumentCode(urn.getRegistrarCode(), urn.getDocumentCode(), true);
    }

}

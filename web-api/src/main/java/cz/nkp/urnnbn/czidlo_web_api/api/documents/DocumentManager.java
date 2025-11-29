package cz.nkp.urnnbn.czidlo_web_api.api.documents;

import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.Record;
import cz.nkp.urnnbn.core.dto.UrnNbn;

public interface DocumentManager {

    public Record getRecord(UrnNbn urnNbn);
}

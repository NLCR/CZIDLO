package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.dto.ResolvationLog;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import org.joda.time.DateTime;

import java.util.List;

public interface UrnNbnResolvationLogsDAO {

    public String TABLE_NAME = "urnnbn_resolvation";
    public String ATTR_ID = "id";
    public String ATTR_REGISTRAR_CODE = "registrarCode";
    public String ATTR_DOCUMENT_CODE = "documentCode";
    public String ATTR_RESOLVED = "resolved";

    public ResolvationLog insertResolvationAccessLog(String registrarCode, String documentCode) throws DatabaseException;

    public List<ResolvationLog> getResolvationLogsByTimestamps(DateTime fromInclusive, DateTime untilExclusive) throws DatabaseException;
}

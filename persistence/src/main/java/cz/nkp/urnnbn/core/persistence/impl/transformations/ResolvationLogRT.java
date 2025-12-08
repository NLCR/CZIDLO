package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.dto.ResolvationLog;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.UrnNbnResolvationLogsDAO;
import org.joda.time.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ResolvationLogRT implements ResultsetTransformer {
    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong(UrnNbnResolvationLogsDAO.ATTR_ID);
        String registrarCode = resultSet.getString(UrnNbnResolvationLogsDAO.ATTR_REGISTRAR_CODE);
        String documentCode = resultSet.getString(UrnNbnResolvationLogsDAO.ATTR_DOCUMENT_CODE);
        Timestamp resolvedTs = resultSet.getTimestamp(UrnNbnResolvationLogsDAO.ATTR_RESOLVED);
        DateTime resolved = DateTimeUtils.timestampToDatetime(resolvedTs);
        return new ResolvationLog(id, registrarCode, documentCode, resolved);
    }
}

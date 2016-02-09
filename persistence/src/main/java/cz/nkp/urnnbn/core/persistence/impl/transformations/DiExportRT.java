package cz.nkp.urnnbn.core.persistence.impl.transformations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import cz.nkp.urnnbn.core.DiExport;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;

public class DiExportRT implements ResultsetTransformer {

    @Override
    public DiExport transform(ResultSet resultSet) throws SQLException {
        DiExport result = new DiExport();
        result.setRegistrarCode(resultSet.getString("regCode"));
        result.setDocumentCode(resultSet.getString("docCode"));
        result.setUrnActive(resultSet.getBoolean("urnActive"));
        result.setIeType(resultSet.getString("ieType"));
        result.setDiUrl(resultSet.getString("diUrl"));
        result.setDiActive(resultSet.getBoolean("diActive"));
        result.setDiFormat(resultSet.getString("diFormat"));
        result.setDiAccessiblility(resultSet.getString("diAccessibility"));
        // timestamps
        Timestamp created = resultSet.getTimestamp("diCreated");
        if (created != null) {
            result.setDiCreated(DateTimeUtils.timestampToDatetime(created));
        }
        Timestamp deactivated = resultSet.getTimestamp("diDeactivated");
        if (deactivated != null) {
            result.setDiDeactivated(DateTimeUtils.timestampToDatetime(deactivated));
        }
        return result;
    }

}

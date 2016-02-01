package cz.nkp.urnnbn.core.persistence.impl.transformations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import cz.nkp.urnnbn.core.UrnNbnExport;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;

public class UrnNbnExportRT implements ResultsetTransformer {

    public Object transform(ResultSet rs) throws SQLException {
        UrnNbnExport result = new UrnNbnExport();
        result.setUrnNbn(rs.getString("urn_nbn"));
        // reserved
        Timestamp reserved = rs.getTimestamp("reserved");
        if (reserved != null) {
            result.setReserved(DateTimeUtils.timestampToDatetime(reserved));
        }
        // registered
        Timestamp registered = rs.getTimestamp("registered");
        result.setRegistered(DateTimeUtils.timestampToDatetime(registered));
        // deactivated
        Timestamp deactivated = rs.getTimestamp("deactivated");
        if (deactivated != null) {
            result.setDeactivated(DateTimeUtils.timestampToDatetime(deactivated));
        }
        result.setEntityType(rs.getString("entity_type"));
        result.setCnbAssigned(rs.getBoolean("cnb"));
        result.setIssnAssigned(rs.getBoolean("issn"));
        result.setIsbnAssigned(rs.getBoolean("isbn"));
        result.setActive(rs.getBoolean("active"));
        result.setNumberOfDigitalInstances(rs.getInt("digital_instances"));
        // title data
        result.setTitle(rs.getString("id_title"));
        result.setSubtitle(rs.getString("id_sub_title"));
        result.setVolumeTitle(rs.getString("id_volume_title"));
        result.setIssueTitle(rs.getString("id_issue_title"));
        return result;
    }

}

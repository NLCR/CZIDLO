package cz.nkp.urnnbn.core.persistence.impl.transformations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import cz.nkp.urnnbn.core.dto.UrnNbnExport;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;

public class UrnNbnExportRT implements ResultsetTransformer {

	public Object transform(ResultSet rs) throws SQLException {
		UrnNbnExport result = new UrnNbnExport();
		result.setUrn(rs.getString("urn"));
		Timestamp reserved = rs.getTimestamp("reserved");
		Timestamp modified = rs.getTimestamp("modified");
		if (reserved != null) {
			result.setReserved(DateTimeUtils.timestampToDatetime(reserved));
		}
		if (modified != null) {
			result.setModified(DateTimeUtils.timestampToDatetime(modified));
		}
		result.setEntityType(rs.getString("entitytype"));
		result.setTitle(rs.getString("title"));
		result.setCnbAssigned(rs.getBoolean("cnb"));
		result.setIssnAssigned(rs.getBoolean("issn"));
		result.setIsbnAssigned(rs.getBoolean("isbn"));
		result.setActive(rs.getBoolean("active"));
		result.setNumberOfDigitalInstances(rs.getInt("digitalinstances"));
		return result;
	}

}

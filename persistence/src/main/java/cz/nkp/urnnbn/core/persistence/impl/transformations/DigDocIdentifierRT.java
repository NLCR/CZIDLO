/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.DigDocIdType;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.DigDocIdentifierDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *
 * @author Martin Řehánek
 */
public class DigDocIdentifierRT implements ResultsetTransformer {
    
    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        DigDocIdentifier identifier = new DigDocIdentifier();
        identifier.setRegistrarId(resultSet.getLong(DigDocIdentifierDAO.ATTR_REG_ID));
        if (resultSet.wasNull()) {
            identifier.setRegistrarId(null);
        }
        identifier.setDigDocId(resultSet.getLong(DigDocIdentifierDAO.ATTR_DIG_REP_ID));
        if (resultSet.wasNull()) {
            identifier.setDigDocId(null);
        }
        Timestamp created = resultSet.getTimestamp(DigDocIdentifierDAO.ATTR_CREATED);
        identifier.setCreated(DateTimeUtils.timestampToDatetime(created));
        Timestamp updated = resultSet.getTimestamp(DigDocIdentifierDAO.ATTR_UPDATED);
        identifier.setModified(DateTimeUtils.timestampToDatetime(updated));
        identifier.setType(DigDocIdType.valueOf(resultSet.getString(DigDocIdentifierDAO.ATTR_TYPE)));
        identifier.setValue(resultSet.getString(DigDocIdentifierDAO.ATTR_VALUE));
        return identifier;
    }
}

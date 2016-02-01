/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.RegistrarScopeIdentifierDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarScopeIdentifierRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        RegistrarScopeIdentifier identifier = new RegistrarScopeIdentifier();
        identifier.setRegistrarId(resultSet.getLong(RegistrarScopeIdentifierDAO.ATTR_REG_ID));
        if (resultSet.wasNull()) {
            identifier.setRegistrarId(null);
        }
        identifier.setDigDocId(resultSet.getLong(RegistrarScopeIdentifierDAO.ATTR_DIG_DOC_ID));
        if (resultSet.wasNull()) {
            identifier.setDigDocId(null);
        }
        Timestamp created = resultSet.getTimestamp(RegistrarScopeIdentifierDAO.ATTR_CREATED);
        identifier.setCreated(DateTimeUtils.timestampToDatetime(created));
        Timestamp updated = resultSet.getTimestamp(RegistrarScopeIdentifierDAO.ATTR_UPDATED);
        identifier.setModified(DateTimeUtils.timestampToDatetime(updated));
        identifier.setType(RegistrarScopeIdType.valueOf(resultSet.getString(RegistrarScopeIdentifierDAO.ATTR_TYPE)));
        identifier.setValue(resultSet.getString(RegistrarScopeIdentifierDAO.ATTR_VALUE));
        return identifier;
    }
}

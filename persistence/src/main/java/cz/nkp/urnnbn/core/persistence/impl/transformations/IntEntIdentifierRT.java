/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.persistence.IntEntIdentifierDAO;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class IntEntIdentifierRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        IntEntIdentifier identifier = new IntEntIdentifier();
        identifier.setIntEntDbId(resultSet.getLong(IntEntIdentifierDAO.ATTR_IE_ID));
        if (resultSet.wasNull()) {
            identifier.setIntEntDbId(null);
        }
        identifier.setType(IntEntIdType.valueOf(resultSet.getString(IntEntIdentifierDAO.ATTR_TYPE)));
        identifier.setValue(resultSet.getString(IntEntIdentifierDAO.ATTR_VALUE));
        return identifier;
    }
}

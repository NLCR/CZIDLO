/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.DigRepIdType;
import cz.nkp.urnnbn.core.dto.DigRepIdentifier;
import cz.nkp.urnnbn.core.persistence.DigRepIdentifierDAO;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class DigRepIdentifierRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        DigRepIdentifier identifier = new DigRepIdentifier();
        identifier.setRegistrarId(resultSet.getLong(DigRepIdentifierDAO.ATTR_REG_ID));
        identifier.setDigRepId(resultSet.getLong(DigRepIdentifierDAO.ATTR_DIG_REP_ID));
        identifier.setType(DigRepIdType.valueOf(resultSet.getString(DigRepIdentifierDAO.ATTR_TYPE)));
        identifier.setValue(resultSet.getString(DigRepIdentifierDAO.ATTR_VALUE));
        return identifier;
    }
}

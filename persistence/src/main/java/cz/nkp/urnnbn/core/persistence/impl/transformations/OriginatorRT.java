/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.OriginType;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.persistence.OriginatorDAO;

/**
 *
 * @author Martin Řehánek
 */
public class OriginatorRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        Originator originator = new Originator();
        originator.setIntEntId(resultSet.getLong(OriginatorDAO.ATTR_INT_ENT_ID));
        if (resultSet.wasNull()) {
            originator.setIntEntId(null);
        }
        originator.setType(OriginType.valueOf(resultSet.getString(OriginatorDAO.ATTR_TYPE)));
        originator.setValue(resultSet.getString(OriginatorDAO.ATTR_VALUE));
        return originator;
    }
}

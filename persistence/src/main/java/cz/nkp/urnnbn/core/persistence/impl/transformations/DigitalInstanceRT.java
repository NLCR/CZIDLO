/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.persistence.DigitalInstanceDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.rmi.CORBA.Util;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalInstanceRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        DigitalInstance instance = new DigitalInstance();
        instance.setId(resultSet.getLong(DigitalInstanceDAO.ATTR_ID));
        instance.setDigRepId(resultSet.getLong(DigitalInstanceDAO.ATTR_DIG_REP_ID));
        instance.setLibraryId(resultSet.getLong(DigitalInstanceDAO.ATTR_LIB_ID));
        instance.setPublished(DateTimeUtils.timestampToDatetime(resultSet.getTimestamp(DigitalInstanceDAO.ATTR_PUBLISHED)));
        instance.setUrl(resultSet.getString(DigitalInstanceDAO.ATTR_URL));
        return instance;
    }
}

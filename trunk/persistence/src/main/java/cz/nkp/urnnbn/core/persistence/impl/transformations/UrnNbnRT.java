/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.UrnNbnDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        long digRepId = resultSet.getLong(UrnNbnDAO.ATTR_DIG_DOC_ID);
        DateTime created = DateTimeUtils.timestampToDatetime(
                resultSet.getTimestamp(UrnNbnDAO.ATTR_CREATED));
        DateTime updated = DateTimeUtils.timestampToDatetime(
                resultSet.getTimestamp(UrnNbnDAO.ATTR_UPDATED));
        RegistrarCode registrarCode = RegistrarCode.valueOf(resultSet.getString(UrnNbnDAO.ATTR_REGISTRAR_CODE));
        String documentCode = resultSet.getString(UrnNbnDAO.ATTR_DOCUMENT_CODE);
        
        return new UrnNbn(registrarCode, documentCode, digRepId, created, updated);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.Utils;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.UrnNbnReservedDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnReservedRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        String registrarCode = resultSet.getString(UrnNbnReservedDAO.ATTR_REGISTRAR_CODE);
        String documentCode = resultSet.getString(UrnNbnReservedDAO.ATTR_DOCUMENT_CODE);
        DateTime created = Utils.timestampToDatetime(
                resultSet.getTimestamp(UrnNbnReservedDAO.ATTR_CREATED));
        return new UrnNbn(registrarCode, documentCode, null, created);
    }
}

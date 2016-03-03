/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.UrnNbnDAO;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        Long digDocId = resultSet.getLong(UrnNbnDAO.ATTR_DIG_DOC_ID);
        if (resultSet.wasNull()) {
            digDocId = null;
        }
        Timestamp reserved = resultSet.getTimestamp(UrnNbnDAO.ATTR_RESERVED);
        Timestamp registered = resultSet.getTimestamp(UrnNbnDAO.ATTR_REGISTERED);
        Timestamp deactivated = resultSet.getTimestamp(UrnNbnDAO.ATTR_DEACTIVATED);
        RegistrarCode registrarCode = RegistrarCode.valueOf(resultSet.getString(UrnNbnDAO.ATTR_REGISTRAR_CODE));
        String documentCode = resultSet.getString(UrnNbnDAO.ATTR_DOCUMENT_CODE);
        boolean active = resultSet.getBoolean(UrnNbnDAO.ATTR_ACTIVE);
        String deactivationNote = resultSet.getString(UrnNbnDAO.ATTR_DEACTIVATION_NOTE);
        return new UrnNbn(registrarCode, documentCode, digDocId, reserved == null ? null : DateTimeUtils.timestampToDatetime(reserved),
                registered == null ? null : DateTimeUtils.timestampToDatetime(registered), deactivated == null ? null
                        : DateTimeUtils.timestampToDatetime(deactivated), active, deactivationNote);
    }
}

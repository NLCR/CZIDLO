/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.IntelectualEntityDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 *
 * @author Martin Řehánek
 */
public class UpdateIntEntity implements StatementWrapper {

    private final IntelectualEntity entity;

    public UpdateIntEntity(IntelectualEntity entity) {
        this.entity = entity;
    }

    @Override
    public String preparedStatement() {
        return "UPDATE " + IntelectualEntityDAO.TABLE_NAME + " SET " + IntelectualEntityDAO.ATTR_UPDATED + "=?," + IntelectualEntityDAO.ATTR_DOC_TYPE
                + "=?," + IntelectualEntityDAO.ATTR_DIGITAL_BORN + "=?," + IntelectualEntityDAO.ATTR_OTHER_ORIGINATOR + "=?,"
                + IntelectualEntityDAO.ATTR_DEG_AW_INST + "=?" + " WHERE " + IntelectualEntityDAO.ATTR_ID + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setTimestamp(1, DateTimeUtils.nowTs());
            st.setString(2, entity.getDocumentType());
            st.setBoolean(3, entity.isDigitalBorn() == null ? false : entity.isDigitalBorn());
            st.setString(4, entity.getOtherOriginator());
            st.setString(5, entity.getDegreeAwardingInstitution());
            st.setLong(6, entity.getId());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

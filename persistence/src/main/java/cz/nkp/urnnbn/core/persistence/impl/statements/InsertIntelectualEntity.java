/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import cz.nkp.urnnbn.core.persistence.IntelectualEntityDAO;
import java.sql.Timestamp;

/**
 *
 * @author Martin Řehánek
 */
public class InsertIntelectualEntity implements StatementWrapper {

    private final IntelectualEntity entity;

    public InsertIntelectualEntity(IntelectualEntity entity) {
        this.entity = entity;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + IntelectualEntityDAO.TABLE_NAME
                + "(" + IntelectualEntityDAO.ATTR_ID
                + "," + IntelectualEntityDAO.ATTR_ENTITY_TYPE
                + "," + IntelectualEntityDAO.ATTR_CREATED
                + "," + IntelectualEntityDAO.ATTR_UPDATED
                + "," + IntelectualEntityDAO.ATTR_TITLE
                + "," + IntelectualEntityDAO.ATTR_ALT_TITLE
                + "," + IntelectualEntityDAO.ATTR_DOC_TYPE
                + "," + IntelectualEntityDAO.ATTR_DIGITAL_BORN
                + "," + IntelectualEntityDAO.ATTR_DEG_AW_INST
                + ") values(?,?,?,?,?,?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, entity.getId());
            st.setString(2, entity.getEntityType().name());
            Timestamp now = DateTimeUtils.nowTs();
            st.setTimestamp(3, now);
            st.setTimestamp(4, now);
            st.setString(5, entity.getTitle());
            st.setString(6, entity.getAlternativeTitle());
            st.setString(7, entity.getDocumentType());
            st.setBoolean(8, entity.isDigitalBorn());
            st.setString(9, entity.getDegreeAwardingInstitution());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

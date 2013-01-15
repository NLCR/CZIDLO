/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.DigitalInstanceDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class DeactivateDigitalInstance implements StatementWrapper {

    private final Long digitalInstanceId;

    public DeactivateDigitalInstance(Long digitalInstanceId) {
        this.digitalInstanceId = digitalInstanceId;
    }

    public String preparedStatement() {
        return "UPDATE " + DigitalInstanceDAO.TABLE_NAME
                + " SET "
                + DigitalInstanceDAO.ATTR_ACTIVE + "=?,"
                + DigitalInstanceDAO.ATTR_DEACTIVATED + "=?"
                + " WHERE " + DigitalInstanceDAO.ATTR_ID + "=?";
    }

    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setBoolean(1, false);
            st.setTimestamp(2, DateTimeUtils.nowTs());
            st.setLong(3, digitalInstanceId);
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

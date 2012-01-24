/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.persistence.OriginatorDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class UpdateOriginator implements StatementWrapper {

    private final Originator originator;

    public UpdateOriginator(Originator originator) {
        this.originator = originator;
    }

    @Override
    public String preparedStatement() {
        return "UPDATE " + OriginatorDAO.TABLE_NAME + " SET "
                + OriginatorDAO.ATTR_TYPE + "=?,"
                + OriginatorDAO.ATTR_VALUE + "=?"
                + " WHERE " + OriginatorDAO.ATTR_INT_ENT_ID + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, originator.getType().name());
            st.setString(2, originator.getValue());
            st.setLong(3, originator.getIntEntId());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

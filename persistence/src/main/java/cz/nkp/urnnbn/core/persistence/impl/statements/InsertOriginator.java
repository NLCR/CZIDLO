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
public class InsertOriginator implements StatementWrapper {

    private final Originator originator;

    public InsertOriginator(Originator o) {
        this.originator = o;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + OriginatorDAO.TABLE_NAME + "(" + OriginatorDAO.ATTR_INT_ENT_ID + "," + OriginatorDAO.ATTR_TYPE + ","
                + OriginatorDAO.ATTR_VALUE + ") values(?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, originator.getIntEntId());
            st.setString(2, originator.getType().name());
            st.setString(3, originator.getValue());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

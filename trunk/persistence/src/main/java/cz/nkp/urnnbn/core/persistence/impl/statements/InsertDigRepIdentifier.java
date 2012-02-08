/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.persistence.DigDocIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class InsertDigRepIdentifier implements StatementWrapper {

    private final DigDocIdentifier identifier;

    public InsertDigRepIdentifier(DigDocIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + DigDocIdentifierDAO.TABLE_NAME
                + "(" + DigDocIdentifierDAO.ATTR_DIG_REP_ID
                + "," + DigDocIdentifierDAO.ATTR_REG_ID
                + "," + DigDocIdentifierDAO.ATTR_TYPE
                + "," + DigDocIdentifierDAO.ATTR_VALUE
                + ") values(?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, identifier.getDigDocId());
            st.setLong(2, identifier.getRegistrarId());
            st.setString(3, identifier.getType().toString());
            st.setString(4, identifier.getValue());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

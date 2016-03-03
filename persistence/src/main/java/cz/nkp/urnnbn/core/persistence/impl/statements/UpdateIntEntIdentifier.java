/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.persistence.IntEntIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 *
 * @author Martin Řehánek
 */
public class UpdateIntEntIdentifier implements StatementWrapper {

    private final IntEntIdentifier identifier;

    public UpdateIntEntIdentifier(IntEntIdentifier identifier) {
        this.identifier = identifier;
    }

    public String preparedStatement() {
        return "UPDATE " + IntEntIdentifierDAO.TABLE_NAME + " SET " + IntEntIdentifierDAO.ATTR_VALUE + "=?" + " WHERE "
                + IntEntIdentifierDAO.ATTR_IE_ID + "=?" + " AND " + IntEntIdentifierDAO.ATTR_TYPE + "=?";
    }

    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, identifier.getValue());
            st.setLong(2, identifier.getIntEntDbId());
            st.setString(3, identifier.getType().name());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.IntEntIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class InsertIntEntIdentifier implements StatementWrapper {

    private final IntEntIdentifier identifier;

    public InsertIntEntIdentifier(IntEntIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + IntEntIdentifierDAO.TABLE_NAME
                + "(" + IntEntIdentifierDAO.ATTR_IE_ID
                + "," + IntEntIdentifierDAO.ATTR_TYPE
                + "," + IntEntIdentifierDAO.ATTR_VALUE
                + ") values(?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, identifier.getIntEntDbId());
            st.setString(2, identifier.getType().name());
            st.setString(3, identifier.getValue());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

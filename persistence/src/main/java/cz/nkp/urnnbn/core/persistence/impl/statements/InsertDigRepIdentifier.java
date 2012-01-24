/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.DigRepIdentifier;
import cz.nkp.urnnbn.core.persistence.DigRepIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class InsertDigRepIdentifier implements StatementWrapper {

    private final DigRepIdentifier identifier;

    public InsertDigRepIdentifier(DigRepIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + DigRepIdentifierDAO.TABLE_NAME
                + "(" + DigRepIdentifierDAO.ATTR_DIG_REP_ID
                + "," + DigRepIdentifierDAO.ATTR_REG_ID
                + "," + DigRepIdentifierDAO.ATTR_TYPE
                + "," + DigRepIdentifierDAO.ATTR_VALUE
                + ") values(?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, identifier.getDigRepId());
            st.setLong(2, identifier.getRegistrarId());
            st.setString(3, identifier.getType().toString());
            st.setString(4, identifier.getValue());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

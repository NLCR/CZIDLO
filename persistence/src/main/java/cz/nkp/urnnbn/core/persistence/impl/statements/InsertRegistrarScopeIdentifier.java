/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.RegistrarScopeIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 *
 * @author Martin Řehánek
 */
public class InsertRegistrarScopeIdentifier implements StatementWrapper {

    private final RegistrarScopeIdentifier identifier;

    public InsertRegistrarScopeIdentifier(RegistrarScopeIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + RegistrarScopeIdentifierDAO.TABLE_NAME + "(" + RegistrarScopeIdentifierDAO.ATTR_DIG_DOC_ID + ","
                + RegistrarScopeIdentifierDAO.ATTR_REG_ID + "," + RegistrarScopeIdentifierDAO.ATTR_CREATED + ","
                + RegistrarScopeIdentifierDAO.ATTR_UPDATED + "," + RegistrarScopeIdentifierDAO.ATTR_TYPE + ","
                + RegistrarScopeIdentifierDAO.ATTR_VALUE + ") values(?,?,?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, identifier.getDigDocId());
            st.setLong(2, identifier.getRegistrarId());
            Timestamp now = DateTimeUtils.nowTs();
            st.setTimestamp(3, now);
            st.setTimestamp(4, now);
            st.setString(5, identifier.getType().toString());
            st.setString(6, identifier.getValue().toString());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

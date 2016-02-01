/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.RegistrarScopeIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class UpdateRegistrarScopeIdentifier implements StatementWrapper {

    private final RegistrarScopeIdentifier id;

    public UpdateRegistrarScopeIdentifier(RegistrarScopeIdentifier id) {
        this.id = id;
    }

    @Override
    public String preparedStatement() {
        return "UPDATE " + RegistrarScopeIdentifierDAO.TABLE_NAME + " SET " + RegistrarScopeIdentifierDAO.ATTR_UPDATED + "=?,"
                + RegistrarScopeIdentifierDAO.ATTR_VALUE + "=?" + " WHERE " + RegistrarScopeIdentifierDAO.ATTR_REG_ID + "=?" + " AND "
                + RegistrarScopeIdentifierDAO.ATTR_DIG_DOC_ID + "=?" + " AND " + RegistrarScopeIdentifierDAO.ATTR_TYPE + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setTimestamp(1, DateTimeUtils.nowTs());
            st.setString(2, id.getValue());
            st.setLong(3, id.getRegistrarId());
            st.setLong(4, id.getDigDocId());
            st.setString(5, id.getType().toString());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

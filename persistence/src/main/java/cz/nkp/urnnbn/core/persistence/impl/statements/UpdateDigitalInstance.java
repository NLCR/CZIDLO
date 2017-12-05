package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.persistence.DigitalInstanceDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateDigitalInstance implements StatementWrapper {

    private final DigitalInstance instance;

    public UpdateDigitalInstance(DigitalInstance instance) {
        this.instance = instance;
    }

    public String preparedStatement() {
        return "UPDATE " + DigitalInstanceDAO.TABLE_NAME + " SET "
                + DigitalInstanceDAO.ATTR_ACTIVE + "=?,"
                + DigitalInstanceDAO.ATTR_URL + "=?,"
                + DigitalInstanceDAO.ATTR_FORMAT + "=?,"
                + DigitalInstanceDAO.ATTR_ACCESS + "=?,"
                + DigitalInstanceDAO.ATTR_ACCESS_RESTRICTION + "=?"
                + " WHERE " + DigitalInstanceDAO.ATTR_ID + "=?";
    }

    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setBoolean(1, instance.isActive());
            st.setString(2, instance.getUrl());
            st.setString(3, instance.getFormat());
            st.setString(4, instance.getAccessibility());
            st.setInt(5, instance.getAccessRestriction().ordinal());
            st.setLong(6, instance.getId());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }

}

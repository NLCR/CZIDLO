package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.persistence.DigitalInstanceDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

public class UpdateDigitalInstance implements StatementWrapper {

	private final DigitalInstance instance;

    public UpdateDigitalInstance(DigitalInstance instance) {
        this.instance = instance;
    }
	
	public String preparedStatement() {
		return "UPDATE " + DigitalInstanceDAO.TABLE_NAME + " SET "
                + DigitalInstanceDAO.ATTR_DIG_DOC_ID + "=?,"
                + DigitalInstanceDAO.ATTR_LIB_ID + "=?,"
                + DigitalInstanceDAO.ATTR_ACTIVE + "=?,"
                + DigitalInstanceDAO.ATTR_URL + "=?,"
                + DigitalInstanceDAO.ATTR_FORMAT + "=?,"
                + DigitalInstanceDAO.ATTR_ACCESS + "=?"
                + " WHERE " + DigitalInstanceDAO.ATTR_ID + "=?";
	}

	public void populate(PreparedStatement st) throws SyntaxException {
		try {
            st.setLong(1, instance.getDigDocId());
            st.setLong(2, instance.getLibraryId());
            st.setBoolean(3, instance.isActive());
            st.setString(4, instance.getUrl());
            st.setString(5, instance.getFormat());
            st.setString(6, instance.getAccessibility());
            st.setLong(7, instance.getId());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
	}

}

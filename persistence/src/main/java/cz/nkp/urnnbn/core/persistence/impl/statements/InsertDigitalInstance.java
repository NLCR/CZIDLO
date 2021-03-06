/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.DigitalInstanceDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Martin Řehánek
 */
public class InsertDigitalInstance implements StatementWrapper {

    private final DigitalInstance instance;

    public InsertDigitalInstance(DigitalInstance instance) {
        this.instance = instance;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + DigitalInstanceDAO.TABLE_NAME + "("
                + DigitalInstanceDAO.ATTR_ID + ","
                + DigitalInstanceDAO.ATTR_DIG_DOC_ID + ","
                + DigitalInstanceDAO.ATTR_LIB_ID + ","
                + DigitalInstanceDAO.ATTR_CREATED + ","
                + DigitalInstanceDAO.ATTR_ACTIVE + ","
                + DigitalInstanceDAO.ATTR_URL + ","
                + DigitalInstanceDAO.ATTR_FORMAT + ","
                + DigitalInstanceDAO.ATTR_ACCESS + ","
                + DigitalInstanceDAO.ATTR_ACCESS_RESTRICTION
                + ") values(?,?,?,?,?,?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, instance.getId());
            st.setLong(2, instance.getDigDocId());
            st.setLong(3, instance.getLibraryId());
            st.setTimestamp(4, DateTimeUtils.nowTs());
            st.setBoolean(5, instance.isActive());
            st.setString(6, instance.getUrl());
            st.setString(7, instance.getFormat());
            st.setString(8, instance.getAccessibility());
            st.setInt(9, instance.getAccessRestriction().ordinal());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

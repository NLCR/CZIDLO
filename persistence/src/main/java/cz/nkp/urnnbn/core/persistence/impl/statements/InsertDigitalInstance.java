/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.persistence.DigitalInstanceDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class InsertDigitalInstance implements StatementWrapper {

    private final DigitalInstance i;

    public InsertDigitalInstance(DigitalInstance i) {
        this.i = i;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + DigitalInstanceDAO.TABLE_NAME
                + "(" + DigitalInstanceDAO.ATTR_ID
                + "," + DigitalInstanceDAO.ATTR_DIG_DOC_ID
                + "," + DigitalInstanceDAO.ATTR_LIB_ID
                + "," + DigitalInstanceDAO.ATTR_URL
                + "," + DigitalInstanceDAO.ATTR_PUBLISHED
                + "," + DigitalInstanceDAO.ATTR_ACCESS
                + ") values(?,?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, i.getId());
            st.setLong(2, i.getDigDocId());
            st.setLong(3, i.getLibraryId());
            st.setString(4, i.getUrl());
            st.setTimestamp(5, DateTimeUtils.nowTs());
            st.setString(6, i.getAccessibility());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

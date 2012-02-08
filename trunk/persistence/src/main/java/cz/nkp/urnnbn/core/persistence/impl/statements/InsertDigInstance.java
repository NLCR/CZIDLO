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
public class InsertDigInstance implements StatementWrapper {

    private final DigitalInstance instance;

    public InsertDigInstance(DigitalInstance instance) {
        this.instance = instance;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + DigitalInstanceDAO.TABLE_NAME
                + "(" + DigitalInstanceDAO.ATTR_ID
                + "," + DigitalInstanceDAO.ATTR_DIG_REP_ID
                + "," + DigitalInstanceDAO.ATTR_LIB_ID
                + "," + DigitalInstanceDAO.ATTR_URL
                + "," + DigitalInstanceDAO.ATTR_PUBLISHED
                + ") values(?,?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, instance.getId());
            st.setLong(2, instance.getDigDocId());
            st.setLong(3, instance.getLibraryId());
            st.setString(4, instance.getUrl());
            st.setTimestamp(5, DateTimeUtils.nowTs());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

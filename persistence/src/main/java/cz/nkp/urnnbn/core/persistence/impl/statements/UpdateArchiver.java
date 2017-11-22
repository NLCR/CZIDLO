/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.persistence.ArchiverDAO;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Martin Řehánek
 */
public class UpdateArchiver implements StatementWrapper {

    private final Archiver archiver;

    public UpdateArchiver(Archiver archiver) {
        this.archiver = archiver;
    }

    @Override
    public String preparedStatement() {
        return "UPDATE " + ArchiverDAO.TABLE_NAME + " SET " + ArchiverDAO.ATTR_UPDATED + "=?," + ArchiverDAO.ATTR_NAME + "=?,"
                + ArchiverDAO.ATTR_DESCRIPTION + "=?," + ArchiverDAO.ATTR_HIDDEN + "=?" + " WHERE "
                + ArchiverDAO.ATTR_ID + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setTimestamp(1, DateTimeUtils.nowTs());
            st.setString(2, archiver.getName());
            st.setString(3, archiver.getDescription());
            st.setBoolean(4, archiver.isHidden());
            st.setLong(5, archiver.getId());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.DigitalLibraryDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class UpdateLibrary implements StatementWrapper {

    private final DigitalLibrary library;

    public UpdateLibrary(DigitalLibrary library) {
        this.library = library;
    }

    @Override
    public String preparedStatement() {
        return "UPDATE " + DigitalLibraryDAO.TABLE_NAME + " SET " + DigitalLibraryDAO.ATTR_UPDATED + "=?," + DigitalLibraryDAO.ATTR_NAME + "=?,"
                + DigitalLibraryDAO.ATTR_DESCRIPTION + "=?," + DigitalLibraryDAO.ATTR_URL + "=?" + " WHERE " + DigitalLibraryDAO.ATTR_ID + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setTimestamp(1, DateTimeUtils.nowTs());
            st.setString(2, library.getName());
            st.setString(3, library.getDescription());
            st.setString(4, library.getUrl());
            st.setLong(5, library.getId());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

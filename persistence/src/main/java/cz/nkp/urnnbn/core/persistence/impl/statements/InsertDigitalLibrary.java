/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import cz.nkp.urnnbn.core.persistence.DigitalLibraryDAO;
import java.sql.Timestamp;

/**
 *
 * @author Martin Řehánek
 */
public class InsertDigitalLibrary implements StatementWrapper {

    private final DigitalLibrary library;

    public InsertDigitalLibrary(DigitalLibrary library) {
        this.library = library;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + DigitalLibraryDAO.TABLE_NAME + "(" + DigitalLibraryDAO.ATTR_ID + "," + DigitalLibraryDAO.ATTR_REGISTRAR_ID + ","
                + DigitalLibraryDAO.ATTR_CREATED + "," + DigitalLibraryDAO.ATTR_UPDATED + "," + DigitalLibraryDAO.ATTR_NAME + ","
                + DigitalLibraryDAO.ATTR_DESCRIPTION + "," + DigitalLibraryDAO.ATTR_URL + ") values(?,?,?,?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, library.getId());
            st.setLong(2, library.getRegistrarId());
            Timestamp now = DateTimeUtils.nowTs();
            st.setTimestamp(3, now);
            st.setTimestamp(4, now);
            st.setString(5, library.getName());
            st.setString(6, library.getDescription());
            st.setString(7, library.getUrl());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

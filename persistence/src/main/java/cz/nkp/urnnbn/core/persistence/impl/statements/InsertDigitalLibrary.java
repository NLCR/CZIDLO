/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import cz.nkp.urnnbn.core.persistence.DigitalLibraryDAO;

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
        return "INSERT into " + DigitalLibraryDAO.TABLE_NAME
                + "(" + DigitalLibraryDAO.ATTR_ID
                + "," + DigitalLibraryDAO.ATTR_REGISTRAR_ID
                + "," + DigitalLibraryDAO.ATTR_NAME
                + "," + DigitalLibraryDAO.ATTR_DESCRIPTION
                + "," + DigitalLibraryDAO.ATTR_URL
                + ") values(?,?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, library.getId());
            st.setLong(2, library.getRegistrarId());
            st.setString(3, library.getName());
            st.setString(4, library.getDescription());
            st.setString(5, library.getUrl());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

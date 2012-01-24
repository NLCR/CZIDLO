/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.ArchiverDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class UpdateArchiver implements StatementWrapper {

    private final Archiver archiver;

    public UpdateArchiver(Archiver archiver) {
        this.archiver = archiver;
    }

    @Override
    public String preparedStatement() {
        return "UPDATE " + ArchiverDAO.TABLE_NAME + " SET "
                + ArchiverDAO.ATTR_NAME + "=?,"
                + ArchiverDAO.ATTR_DESCRIPTION + "=?"
                + " WHERE " + ArchiverDAO.ATTR_ID + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, archiver.getName());
            st.setString(2, archiver.getDescription());
            st.setLong(3, archiver.getId());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

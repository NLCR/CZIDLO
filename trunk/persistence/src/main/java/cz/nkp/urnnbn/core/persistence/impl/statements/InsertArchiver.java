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
public class InsertArchiver implements StatementWrapper {

    private final Archiver archiver;

    public InsertArchiver(Archiver archiver) {
        this.archiver = archiver;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + ArchiverDAO.TABLE_NAME
                + "(" + ArchiverDAO.ATTR_ID
                + "," + ArchiverDAO.ATTR_NAME
                + "," + ArchiverDAO.ATTR_DESCRIPTION
                + ") values(?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, archiver.getId());
            st.setString(2, archiver.getName());
            st.setString(3, archiver.getDescription());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

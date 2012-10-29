/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.persistence.PublicationDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class UpdatePublication extends AbstractStatement implements StatementWrapper {

    private final Publication publication;

    public UpdatePublication(Publication publication) {
        this.publication = publication;
    }

    @Override
    public String preparedStatement() {
        return "UPDATE " + PublicationDAO.TABLE_NAME + " SET "
                + PublicationDAO.ATTR_PLACE + "=?,"
                + PublicationDAO.ATTR_PUBLISHER + "=?,"
                + PublicationDAO.ATTR_YEAR + "=?"
                + " WHERE " + PublicationDAO.ATTR_INT_ENT_ID + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, publication.getPlace());
            st.setString(2, publication.getPublisher());
            setIntOrNull(st, 3, publication.getYear());
            st.setLong(4, publication.getId());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

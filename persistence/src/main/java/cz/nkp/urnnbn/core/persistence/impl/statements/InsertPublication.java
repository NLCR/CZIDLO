/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.PublicationDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class InsertPublication implements StatementWrapper {

    private final Publication publication;

    public InsertPublication(Publication publication) {
        this.publication = publication;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + PublicationDAO.TABLE_NAME
                + "(" + PublicationDAO.ATTR_INT_ENT_ID
                + "," + PublicationDAO.ATTR_YEAR
                + "," + PublicationDAO.ATTR_PLACE
                + "," + PublicationDAO.ATTR_PUBLISHER
                + ") values(?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, publication.getId());
            st.setInt(2, publication.getYear());
            st.setString(3, publication.getPlace());
            st.setString(4, publication.getPublisher());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

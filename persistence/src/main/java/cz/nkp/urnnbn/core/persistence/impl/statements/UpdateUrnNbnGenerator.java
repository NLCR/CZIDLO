/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.dto.UrnNbnGenerator;
import cz.nkp.urnnbn.core.persistence.UrnNbnGeneratorDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 *
 * @author Martin Řehánek
 */
public class UpdateUrnNbnGenerator implements StatementWrapper {

    private final UrnNbnGenerator search;

    public UpdateUrnNbnGenerator(UrnNbnGenerator booking) {
        this.search = booking;
    }

    @Override
    public String preparedStatement() {
        return "UPDATE " + UrnNbnGeneratorDAO.TABLE_NAME + " SET " + UrnNbnGeneratorDAO.ATTR_LAST_DOCUMENT_CODE + "=?" + " WHERE "
                + UrnNbnGeneratorDAO.ATTR_REGISTRAR_ID + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, search.getLastDocumentCode());
            st.setLong(2, search.getRegistrarId());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

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
public class InsertUrnNbnGenerator implements StatementWrapper {

    private final UrnNbnGenerator search;

    public InsertUrnNbnGenerator(UrnNbnGenerator search) {
        this.search = search;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + UrnNbnGeneratorDAO.TABLE_NAME + "(" + UrnNbnGeneratorDAO.ATTR_REGISTRAR_ID + ","
                + UrnNbnGeneratorDAO.ATTR_LAST_DOCUMENT_CODE + ") values(?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, search.getRegistrarId());
            st.setString(2, search.getLastDocumentCode());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

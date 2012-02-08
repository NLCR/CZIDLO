/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import cz.nkp.urnnbn.core.persistence.UrnNbnDAO;
import java.sql.Timestamp;

/**
 *
 * @author Martin Řehánek
 */
public class InsertUrnNbn implements StatementWrapper {

    private final UrnNbn urn;

    public InsertUrnNbn(UrnNbn urn) {
        this.urn = urn;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + UrnNbnDAO.TABLE_NAME
                + "(" + UrnNbnDAO.ATTR_DIG_REP_ID
                + "," + UrnNbnDAO.ATTR_REGISTRAR_CODE
                + "," + UrnNbnDAO.ATTR_DOCUMENT_CODE
                + "," + UrnNbnDAO.ATTR_CREATED
                + ") values(?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, urn.getDigDocId());
            st.setString(2, urn.getRegistrarCode());
            st.setString(3, urn.getDocumentCode());
            Timestamp now = DateTimeUtils.nowTs();
            st.setTimestamp(4, now);
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

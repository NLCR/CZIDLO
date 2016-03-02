/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.UrnNbnDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

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
        return "INSERT into " + UrnNbnDAO.TABLE_NAME + "(" + UrnNbnDAO.ATTR_DIG_DOC_ID + "," + UrnNbnDAO.ATTR_RESERVED + ","
                + UrnNbnDAO.ATTR_REGISTERED + "," + UrnNbnDAO.ATTR_DEACTIVATED + "," + UrnNbnDAO.ATTR_REGISTRAR_CODE + ","
                + UrnNbnDAO.ATTR_DOCUMENT_CODE + "," + UrnNbnDAO.ATTR_ACTIVE + ") values(?,?,?,?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, urn.getDigDocId());
            if (urn.getReserved() != null) {
                st.setTimestamp(2, DateTimeUtils.datetimeToTimestamp(urn.getReserved()));
            } else {
                st.setTimestamp(2, null);
            }
            st.setTimestamp(3, DateTimeUtils.nowTs());
            st.setTimestamp(4, null);
            st.setString(5, urn.getRegistrarCode().toString());
            st.setString(6, urn.getDocumentCode());
            st.setBoolean(7, true);
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

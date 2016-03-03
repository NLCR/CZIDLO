/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.UrnNbnReservedDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 *
 * @author Martin Řehánek
 */
public class InsertUrnNbnReserved implements StatementWrapper {

    private final UrnNbn urn;
    private final long registrarId;

    public InsertUrnNbnReserved(UrnNbn urn, long registrarId) {
        this.urn = urn;
        this.registrarId = registrarId;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + UrnNbnReservedDAO.TABLE_NAME + "(" + UrnNbnReservedDAO.ATTR_REGISTRAR_ID + ","
                + UrnNbnReservedDAO.ATTR_REGISTRAR_CODE + "," + UrnNbnReservedDAO.ATTR_DOCUMENT_CODE + "," + UrnNbnReservedDAO.ATTR_CREATED
                + ") values(?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, registrarId);
            st.setString(2, urn.getRegistrarCode().toString());
            st.setString(3, urn.getDocumentCode());
            Timestamp now = DateTimeUtils.nowTs();
            st.setTimestamp(4, now);
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

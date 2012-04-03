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
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public class InsertUrnNbn implements StatementWrapper {
    
    private final UrnNbn urn;
    private final DateTime created;
    
    public InsertUrnNbn(UrnNbn urn, DateTime created) {
        this.urn = urn;
        this.created = created;
    }
    
    @Override
    public String preparedStatement() {
        return "INSERT into " + UrnNbnDAO.TABLE_NAME
                + "(" + UrnNbnDAO.ATTR_DIG_DOC_ID
                + "," + UrnNbnDAO.ATTR_CREATED
                + "," + UrnNbnDAO.ATTR_UPDATED
                + "," + UrnNbnDAO.ATTR_REGISTRAR_CODE
                + "," + UrnNbnDAO.ATTR_DOCUMENT_CODE
                + ") values(?,?,?,?,?)";
    }
    
    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, urn.getDigDocId());
            Timestamp now = DateTimeUtils.nowTs();
            if (created == null) {
                st.setTimestamp(2, now);
            } else {
                st.setTimestamp(2, DateTimeUtils.datetimeToTimestamp(created));
            }
            st.setTimestamp(3, now);
            st.setString(4, urn.getRegistrarCode().toString());
            st.setString(5, urn.getDocumentCode());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

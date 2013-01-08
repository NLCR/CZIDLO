/*
 * Copyright (C) 2012 Martin Řehánek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.UrnNbnDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class InsertUrnNbnPredecessor implements StatementWrapper {

    private final UrnNbn predecessor;
    private final UrnNbn successor;
    private final String note;

    public InsertUrnNbnPredecessor(UrnNbn predecessor, UrnNbn successor, String note) {
        this.predecessor = predecessor;
        this.successor = successor;
        this.note = note;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + UrnNbnDAO.SUCCESSOR_TABLE_NAME
                + "(" + UrnNbnDAO.ATTR_PRECESSOR_REGISTRAR_CODE
                + "," + UrnNbnDAO.ATTR_PRECESSOR_DOCUMENT_CODE
                + "," + UrnNbnDAO.ATTR_SUCCESSOR_REGISTRAR_CODE
                + "," + UrnNbnDAO.ATTR_SUCCESSOR_DOCUMENT_CODE
                + "," + UrnNbnDAO.ATTR_NOTE
                + ") values(?,?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, predecessor.getRegistrarCode().toString());
            st.setString(2, predecessor.getDocumentCode());
            st.setString(3, successor.getRegistrarCode().toString());
            st.setString(4, successor.getDocumentCode());
            st.setString(5, note);
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

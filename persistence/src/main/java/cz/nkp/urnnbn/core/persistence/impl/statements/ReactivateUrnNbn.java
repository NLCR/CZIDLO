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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.persistence.UrnNbnDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 *
 * @author Martin Řehánek
 */
public class ReactivateUrnNbn implements StatementWrapper {

    private final RegistrarCode registrarCode;
    private final String documentCode;

    public ReactivateUrnNbn(RegistrarCode registrarCode, String documentCode) {
        this.registrarCode = registrarCode;
        this.documentCode = documentCode;
    }

    public String preparedStatement() {
        return "UPDATE " + UrnNbnDAO.TABLE_NAME + " SET " + UrnNbnDAO.ATTR_DEACTIVATED + "=?," + UrnNbnDAO.ATTR_ACTIVE + "=?,"
                + UrnNbnDAO.ATTR_DEACTIVATION_NOTE + "=?" + " WHERE " + UrnNbnDAO.ATTR_REGISTRAR_CODE + "=?" + " AND " + UrnNbnDAO.ATTR_DOCUMENT_CODE
                + "=?";
    }

    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setTimestamp(1, null);
            st.setBoolean(2, true);
            st.setString(3, null);
            st.setString(4, registrarCode.toString());
            st.setString(5, documentCode);
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

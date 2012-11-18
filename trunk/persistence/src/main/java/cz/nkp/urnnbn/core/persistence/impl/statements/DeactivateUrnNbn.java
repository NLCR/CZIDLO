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

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.UrnNbnDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class DeactivateUrnNbn implements StatementWrapper {

    private final RegistrarCode registrarCode;
    private final String documentCode;

    public DeactivateUrnNbn(RegistrarCode registrarCode, String documentCode) {
        this.registrarCode = registrarCode;
        this.documentCode = documentCode;
    }

    public String preparedStatement() {
        return "UPDATE " + UrnNbnDAO.TABLE_NAME + " SET "
                + UrnNbnDAO.ATTR_UPDATED + "=?,"
                + UrnNbnDAO.ATTR_ACTIVE + "=?"
                + " WHERE " + UrnNbnDAO.ATTR_REGISTRAR_CODE + "=?" + " AND " + UrnNbnDAO.ATTR_DOCUMENT_CODE + "=?";
    }

    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setTimestamp(1, DateTimeUtils.nowTs());
            st.setBoolean(2, false);
            st.setString(3, registrarCode.toString());
            st.setString(4, documentCode);
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

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

import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 * s
 *
 * @author Martin Řehánek
 */
public class SelectCountByStringString implements StatementWrapper {

    private final String tableName;
    private final String whereAttr1Name;
    private final String whereAttr1Value;
    private final String whereAttr2Name;
    private final String whereAttr2Value;

    public SelectCountByStringString(String tableName, String whereAttr1Name, String whereAttr1Value, String whereAttr2Name, String whereAttr2Value) {
        this.tableName = tableName;
        this.whereAttr1Name = whereAttr1Name;
        this.whereAttr1Value = whereAttr1Value;
        this.whereAttr2Name = whereAttr2Name;
        this.whereAttr2Value = whereAttr2Value;
    }

    @Override
    public String preparedStatement() {
        return "SELECT count(*) from " + tableName + " WHERE " + whereAttr1Name + "=?" + " AND " + whereAttr2Name + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, whereAttr1Value);
            st.setString(2, whereAttr2Value);
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

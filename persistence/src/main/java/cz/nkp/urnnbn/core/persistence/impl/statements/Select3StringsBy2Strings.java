/*
 * Copyright (C) 2013 Martin Řehánek
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

import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class Select3StringsBy2Strings implements StatementWrapper {

    private final String tableName;
    private final String firstStringAttrName;
    private final String firstStringAttrValue;
    private final String secondStringAttrName;
    private final String secondStringAttrValue;
    private final String firstResultName;
    private final String secondResultName;
    private final String thirdResultName;

    public Select3StringsBy2Strings(String tableName, String firstStringAttrName, String firstStringAttrValue, String secondStringAttrName,
            String secondStringAttrValue, String firstResultName, String secondResultName, String thirdResultName) {
        this.tableName = tableName;
        this.firstStringAttrName = firstStringAttrName;
        this.firstStringAttrValue = firstStringAttrValue;
        this.secondStringAttrName = secondStringAttrName;
        this.secondStringAttrValue = secondStringAttrValue;
        this.firstResultName = firstResultName;
        this.secondResultName = secondResultName;
        this.thirdResultName = thirdResultName;
    }

    @Override
    public String preparedStatement() {
        return "SELECT " + firstResultName + "," + secondResultName + "," + thirdResultName + " from " + tableName + " WHERE " + firstStringAttrName
                + "=? AND " + secondStringAttrName + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, firstStringAttrValue);
            st.setString(2, secondStringAttrValue);
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

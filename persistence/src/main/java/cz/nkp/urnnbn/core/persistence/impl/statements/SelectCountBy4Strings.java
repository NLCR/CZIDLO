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

import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class SelectCountBy4Strings implements StatementWrapper {

    private final String tableName;
    private final String firstStringAttrName;
    private final String firstStringAttrValue;
    private final String secondStringAttrName;
    private final String secondStringAttrValue;
    private final String thirdStringAttrName;
    private final String thirdStringAttrValue;
    private final String fourthStringAttrName;
    private final String fourthStringAttrValue;

    public SelectCountBy4Strings(String tableName, String firstStringAttrName, String firstStringAttrValue, String secondStringAttrName,
            String secondStringAttrValue, String thirdStringAttrName, String thirdStringAttrValue, String fourthStringAttrName,
            String fourthStringAttrValue) {
        this.tableName = tableName;
        this.firstStringAttrName = firstStringAttrName;
        this.firstStringAttrValue = firstStringAttrValue;
        this.secondStringAttrName = secondStringAttrName;
        this.secondStringAttrValue = secondStringAttrValue;
        this.thirdStringAttrName = thirdStringAttrName;
        this.thirdStringAttrValue = thirdStringAttrValue;
        this.fourthStringAttrName = fourthStringAttrName;
        this.fourthStringAttrValue = fourthStringAttrValue;
    }

    @Override
    public String preparedStatement() {
        return "SELECT count(*) FROM " + tableName + " WHERE " + firstStringAttrName + "=? AND " + secondStringAttrName + "=? AND "
                + thirdStringAttrName + "=? AND " + fourthStringAttrName + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, firstStringAttrValue);
            st.setString(2, secondStringAttrValue);
            st.setString(3, thirdStringAttrValue);
            st.setString(4, fourthStringAttrValue);
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

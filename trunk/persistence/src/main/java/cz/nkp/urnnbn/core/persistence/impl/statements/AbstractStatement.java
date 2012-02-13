/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 *
 * @author Martin Řehánek
 */
public class AbstractStatement {

    void setIntOrNull(PreparedStatement st, int index, Integer value) throws SQLException {
        if (value != null) {
            st.setInt(index, value);
        } else {
            st.setNull(index, Types.INTEGER);
        }
    }

    void setDoubleOrNull(PreparedStatement st, int index, Double value) throws SQLException {
        if (value != null) {
            st.setDouble(index, value);
        } else {
            st.setNull(index, Types.DOUBLE);
        }
    }
}

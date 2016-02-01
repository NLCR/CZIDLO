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

import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.ArchiverDAO;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class UpdateRegistrar implements StatementWrapper {

    private final Registrar registrar;

    public UpdateRegistrar(Registrar registrar) {
        this.registrar = registrar;
    }

    @Override
    public String preparedStatement() {
        return "UPDATE " + RegistrarDAO.TABLE_NAME + " SET " + RegistrarDAO.ATTR_ALLOWED_REGISTRATION_MODE_BY_REGISTRAR + "=?,"
                + RegistrarDAO.ATTR_ALLOWED_REGISTRATION_MODE_BY_RESOLVER + "=?," + RegistrarDAO.ATTR_ALLOWED_REGISTRATION_MODE_BY_RESERVATION + "=?"
                + " WHERE " + ArchiverDAO.ATTR_ID + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setBoolean(1, registrar.isRegistrationModeAllowed(UrnNbnRegistrationMode.BY_REGISTRAR));
            st.setBoolean(2, registrar.isRegistrationModeAllowed(UrnNbnRegistrationMode.BY_RESOLVER));
            st.setBoolean(3, registrar.isRegistrationModeAllowed(UrnNbnRegistrationMode.BY_RESERVATION));
            st.setLong(4, registrar.getId());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

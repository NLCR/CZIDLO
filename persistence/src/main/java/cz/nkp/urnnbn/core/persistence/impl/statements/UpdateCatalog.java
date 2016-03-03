/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.persistence.CatalogDAO;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 *
 * @author Martin Řehánek
 */
public class UpdateCatalog implements StatementWrapper {

    private final Catalog catalog;

    public UpdateCatalog(Catalog c) {
        this.catalog = c;
    }

    @Override
    public String preparedStatement() {
        return "UPDATE " + CatalogDAO.TABLE_NAME + " SET " + CatalogDAO.ATTR_UPDATED + "=?," + CatalogDAO.ATTR_NAME + "=?," + CatalogDAO.ATTR_DESC
                + "=?," + CatalogDAO.ATTR_URL_PREFIX + "=?" + " WHERE " + CatalogDAO.ATTR_ID + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setTimestamp(1, DateTimeUtils.nowTs());
            st.setString(2, catalog.getName());
            st.setString(3, catalog.getDescription());
            st.setString(4, catalog.getUrlPrefix());
            st.setLong(5, catalog.getId());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

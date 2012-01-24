/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.persistence.CatalogDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
        return "UPDATE " + CatalogDAO.TABLE_NAME + " SET "
                + CatalogDAO.ATTR_NAME + "=?,"
                + CatalogDAO.ATTR_DESC + "=?,"
                + CatalogDAO.ATTR_URL_PREFIX + "=?"
                + " WHERE " + CatalogDAO.ATTR_ID + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, catalog.getName());
            st.setString(2, catalog.getDescription());
            st.setString(3, catalog.getUrlPrefix());
            st.setLong(4, catalog.getId());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

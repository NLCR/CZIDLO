/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.persistence.CatalogDAO;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 *
 * @author Martin Řehánek
 */
public class InsertCatalog implements StatementWrapper {

    private final Catalog catalog;

    public InsertCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + CatalogDAO.TABLE_NAME + "(" + CatalogDAO.ATTR_ID + "," + CatalogDAO.ATTR_REG_ID + "," + CatalogDAO.ATTR_CREATED + ","
                + CatalogDAO.ATTR_UPDATED + "," + CatalogDAO.ATTR_NAME + "," + CatalogDAO.ATTR_DESC + "," + CatalogDAO.ATTR_URL_PREFIX
                + ") values(?,?,?,?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, catalog.getId());
            st.setLong(2, catalog.getRegistrarId());
            Timestamp now = DateTimeUtils.nowTs();
            st.setTimestamp(3, now);
            st.setTimestamp(4, now);
            st.setString(5, catalog.getName());
            st.setString(6, catalog.getDescription());
            st.setString(7, catalog.getUrlPrefix());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}

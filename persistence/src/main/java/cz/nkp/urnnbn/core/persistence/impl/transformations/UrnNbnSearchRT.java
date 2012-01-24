/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.dto.UrnNbnSearch;
import cz.nkp.urnnbn.core.persistence.UrnNbnSearchDAO;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnSearchRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        UrnNbnSearch search = new UrnNbnSearch();
        search.setRegistrarId(resultSet.getLong(UrnNbnSearchDAO.ATTR_REGISTRAR_ID));
        search.setLastFoundDocumentCode(resultSet.getString(UrnNbnSearchDAO.ATTR_LAST_DOCUMENT_CODE));
        return search;
    }
}

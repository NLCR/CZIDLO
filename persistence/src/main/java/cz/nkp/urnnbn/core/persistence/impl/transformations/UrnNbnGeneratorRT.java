/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.dto.UrnNbnGenerator;
import cz.nkp.urnnbn.core.persistence.UrnNbnGeneratorDAO;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnGeneratorRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        UrnNbnGenerator search = new UrnNbnGenerator();
        search.setRegistrarId(resultSet.getLong(UrnNbnGeneratorDAO.ATTR_REGISTRAR_ID));
        if (resultSet.wasNull()) {
            search.setRegistrarId(null);
        }
        search.setLastDocumentCode(resultSet.getString(UrnNbnGeneratorDAO.ATTR_LAST_DOCUMENT_CODE));
        return search;
    }
}

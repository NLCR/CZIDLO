/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.dto.Content;
import cz.nkp.urnnbn.core.persistence.ContentDAO;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author xrosecky
 */
public class ContentRT implements ResultsetTransformer {

    public Object transform(ResultSet resultSet) throws SQLException {
        Content content = new Content();
        content.setId(resultSet.getLong(ContentDAO.ATTR_ID));
        if (resultSet.wasNull()) {
            content.setId(null);
        }
        content.setContent(resultSet.getString(ContentDAO.ATTR_CONTENT));
        content.setLanguage(resultSet.getString(ContentDAO.ATTR_LANG));
        content.setName(resultSet.getString(ContentDAO.ATTR_NAME));
        return content;
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.persistence.SourceDocumentDAO;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class SourceDocumentRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        SourceDocument doc = new SourceDocument();
        doc.setIntEntId(resultSet.getLong(SourceDocumentDAO.ATTR_INT_ENT_ID));
        doc.setTitle(resultSet.getString(SourceDocumentDAO.ATTR_TITLE));
        doc.setVolumeTitle(resultSet.getString(SourceDocumentDAO.ATTR_VOLUME_TITLE));
        doc.setIssueTitle(resultSet.getString(SourceDocumentDAO.ATTR_ISSUE_TITLE));
        doc.setCcnb(resultSet.getString(SourceDocumentDAO.ATTR_CCNB));
        doc.setIsbn(resultSet.getString(SourceDocumentDAO.ATTR_ISBN));
        doc.setIssn(resultSet.getString(SourceDocumentDAO.ATTR_ISSN));
        doc.setOtherId(resultSet.getString(SourceDocumentDAO.ATTR_OTHER_ID));
        doc.setPublicationPlace(resultSet.getString(SourceDocumentDAO.ATTR_PUB_PLACE));
        doc.setPublisher(resultSet.getString(SourceDocumentDAO.ATTR_PUBLISHER));
        doc.setPublicationYear(resultSet.getInt(SourceDocumentDAO.ATTR_PUB_YEAR));
        return doc;
    }
}

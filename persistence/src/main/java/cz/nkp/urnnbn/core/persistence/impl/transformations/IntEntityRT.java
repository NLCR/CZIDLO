/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.persistence.IntelectualEntityDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *
 * @author Martin Řehánek
 */
public class IntEntityRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        IntelectualEntity entity = new IntelectualEntity();
        entity.setId(resultSet.getLong(IntelectualEntityDAO.ATTR_ID));
        if (resultSet.wasNull()) {
            entity.setId(null);
        }
        // entity type
        String entityType = resultSet.getString(IntelectualEntityDAO.ATTR_ENTITY_TYPE);
        entity.setEntityType(EntityType.valueOf(entityType));
        // created
        Timestamp created = resultSet.getTimestamp(IntelectualEntityDAO.ATTR_CREATED);
        entity.setCreated(DateTimeUtils.timestampToDatetime(created));
        // updated
        Timestamp updated = resultSet.getTimestamp(IntelectualEntityDAO.ATTR_UPDATED);
        entity.setModified(DateTimeUtils.timestampToDatetime(updated));
        // other attribures
        entity.setDocumentType(resultSet.getString(IntelectualEntityDAO.ATTR_DOC_TYPE));
        entity.setDigitalBorn(resultSet.getBoolean(IntelectualEntityDAO.ATTR_DIGITAL_BORN));
        if (resultSet.wasNull()) {
            entity.setDigitalBorn(null);
        }
        entity.setOtherOriginator(resultSet.getString(IntelectualEntityDAO.ATTR_OTHER_ORIGINATOR));
        entity.setDegreeAwardingInstitution(resultSet.getString(IntelectualEntityDAO.ATTR_DEG_AW_INST));
        return entity;
    }
}

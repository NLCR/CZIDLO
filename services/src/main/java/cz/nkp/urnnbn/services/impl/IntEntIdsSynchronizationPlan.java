/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.persistence.IntEntIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.exceptions.UnknownIntelectualEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Martin Řehánek
 */
public class IntEntIdsSynchronizationPlan {

    private final IntEntIdentifierDAO dao;
    private List<IntEntIdentifier> toDelete;
    private List<IntEntIdentifier> toInsert = new ArrayList<IntEntIdentifier>();
    private List<IntEntIdentifier> toUpdate = new ArrayList<IntEntIdentifier>();

    IntEntIdsSynchronizationPlan(Collection<IntEntIdentifier> identifiers, Long entityId, IntEntIdentifierDAO dao) throws UnknownIntelectualEntity,
            DatabaseException {
        this.dao = dao;
        Map<IntEntIdType, IntEntIdentifier> idsFromDatabase = intEntIdentifiersFromDatabase(entityId);
        for (IntEntIdentifier id : identifiers) {
            IntEntIdentifier fromDb = idsFromDatabase.get(id.getType());
            if (fromDb == null) {
                toInsert.add(id);
            } else if (!id.getValue().equals(fromDb.getValue())) {
                toUpdate.add(id);
            } else {
                // nothing - identifier value not changed
            }
        }
        if (idsFromDatabase.size() != identifiers.size()) {
            toDelete = identifiersToDelete(idsFromDatabase.values(), toMap(identifiers));
        } else {
            toDelete = Collections.<IntEntIdentifier> emptyList();
        }
    }

    private Map<IntEntIdType, IntEntIdentifier> intEntIdentifiersFromDatabase(Long intEntId) throws DatabaseException, UnknownIntelectualEntity {
        try {
            List<IntEntIdentifier> idList = dao.getIdList(intEntId);
            return toMap(idList);
        } catch (RecordNotFoundException ex) {
            throw new UnknownIntelectualEntity(intEntId);
        }
    }

    private List<IntEntIdentifier> identifiersToDelete(Collection<IntEntIdentifier> inDatabase, Map<IntEntIdType, IntEntIdentifier> toSynchronize) {
        List<IntEntIdentifier> result = new ArrayList<IntEntIdentifier>();
        for (IntEntIdentifier id : inDatabase) {
            if (!toSynchronize.containsKey(id.getType())) {
                result.add(id);
            }
        }
        return result;
    }

    private Map<IntEntIdType, IntEntIdentifier> toMap(Collection<IntEntIdentifier> identifiers) {
        Map<IntEntIdType, IntEntIdentifier> result = new EnumMap<IntEntIdType, IntEntIdentifier>(IntEntIdType.class);
        for (IntEntIdentifier id : identifiers) {
            result.put(id.getType(), id);
        }
        return result;
    }

    public List<IntEntIdentifier> toDelete() {
        return toDelete;
    }

    public List<IntEntIdentifier> toInsert() {
        return toInsert;
    }

    public List<IntEntIdentifier> toUpdate() {
        return toUpdate;
    }
}

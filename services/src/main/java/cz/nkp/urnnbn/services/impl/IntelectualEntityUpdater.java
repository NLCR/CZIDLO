/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.dto.*;
import cz.nkp.urnnbn.core.persistence.DAOFactory;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.exceptions.UnknownIntelectualEntity;
import cz.nkp.urnnbn.solr_indexer.SolrIndexer;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Martin Řehánek
 */
public class IntelectualEntityUpdater {

    private static final Logger logger = Logger.getLogger(IntelectualEntityUpdater.class.getName());
    private final DAOFactory daoFactory;
    private final SolrIndexer solrIndexer;

    public IntelectualEntityUpdater(DAOFactory daoFactory, SolrIndexer solrIndexer) {
        this.daoFactory = daoFactory;
        this.solrIndexer = solrIndexer;
    }

    void run(IntelectualEntity entity, Originator originator, Publication publication, SourceDocument srcDoc, Collection<IntEntIdentifier> identifiers, UrnNbn urnNbn, Long ddId)
            throws UnknownIntelectualEntity {
        try {
            synchronizeIdentifiers(identifiers, entity.getId());
            updateEntity(entity);
            synchronizeOriginator(originator, entity.getId());
            synchronizePublication(publication, entity.getId());
            synchronizeSrcDoc(srcDoc, entity.getId());
            reindexDigitalDocument(ddId, urnNbn);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void reindexDigitalDocument(long digDocId, UrnNbn urnNbn) { //this should never break the import itself
        try {
            solrIndexer.indexDocument(digDocId);
            logger.log(Level.INFO, "Indexed {0} ", urnNbn.toString());
        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Error indexing " + urnNbn.toString(), e);
        }
    }

    private void updateEntity(IntelectualEntity entity) throws DatabaseException, UnknownIntelectualEntity {
        try {
            daoFactory.intelectualEntityDao().updateEntity(entity);
        } catch (RecordNotFoundException ex) {
            throw new UnknownIntelectualEntity(entity.getId());
        }
    }

    private void synchronizeOriginator(Originator originator, Long entityId) throws DatabaseException, UnknownIntelectualEntity {
        if (originator != null) {
            if (daoFactory.originatorDao().originatorExists(originator.getId())) {
                // update
                try {
                    daoFactory.originatorDao().updateOriginator(originator);
                } catch (RecordNotFoundException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            } else {
                // insert
                try {
                    daoFactory.originatorDao().insertOriginator(originator);
                } catch (RecordNotFoundException ex) {
                    throw new UnknownIntelectualEntity(entityId);
                } catch (AlreadyPresentException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        } else {
            // delete
            daoFactory.originatorDao().removeOriginator(entityId);
        }
    }

    private void synchronizePublication(Publication publication, Long entityId) throws DatabaseException, UnknownIntelectualEntity {
        if (publication != null) {
            if (daoFactory.publicationDao().publicationExists(entityId)) {
                // update
                try {
                    logger.log(Level.FINE, "updating {0}", publication);
                    daoFactory.publicationDao().updatePublication(publication);
                } catch (RecordNotFoundException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            } else {
                // insert
                try {
                    logger.log(Level.FINE, "inserting {0}", publication);
                    daoFactory.publicationDao().insertPublication(publication);
                } catch (AlreadyPresentException ex) {
                    logger.log(Level.SEVERE, null, ex);
                } catch (RecordNotFoundException ex) {
                    throw new UnknownIntelectualEntity(entityId);
                }
            }
        } else {
            // delete
            logger.log(Level.FINE, "deleting publication {0}", entityId);
            daoFactory.publicationDao().removePublication(entityId);
        }
    }

    private void synchronizeSrcDoc(SourceDocument srcDoc, Long entityId) throws DatabaseException, UnknownIntelectualEntity {
        if (srcDoc != null) {
            if (daoFactory.srcDocDao().srcDocExists(entityId)) {
                // update
                try {
                    logger.log(Level.FINE, "updating {0}", srcDoc);
                    daoFactory.srcDocDao().updateSrcDoc(srcDoc);
                } catch (RecordNotFoundException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            } else {
                // insert
                try {
                    logger.log(Level.FINE, "inserting {0}", srcDoc);
                    daoFactory.srcDocDao().insertSrcDoc(srcDoc);
                } catch (AlreadyPresentException ex) {
                    logger.log(Level.SEVERE, null, ex);
                } catch (RecordNotFoundException ex) {
                    throw new UnknownIntelectualEntity(entityId);
                }
            }
        }
    }

    private void synchronizeIdentifiers(Collection<IntEntIdentifier> identifiers, Long entityId) throws UnknownIntelectualEntity, DatabaseException {
        if (identifiers != null) {
            IntEntIdsSynchronizationPlan plan = new IntEntIdsSynchronizationPlan(identifiers, entityId, daoFactory.intEntIdentifierDao());
            // insert identifiers
            for (IntEntIdentifier id : plan.toInsert()) {
                try {
                    logger.log(Level.FINE, "inserting {0}", id);
                    daoFactory.intEntIdentifierDao().insertIntEntId(id);
                } catch (RecordNotFoundException ex) {
                    throw new UnknownIntelectualEntity(entityId);
                } catch (AlreadyPresentException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
            // update identifier values
            for (IntEntIdentifier id : plan.toUpdate()) {
                try {
                    logger.log(Level.FINE, "updating {0}", id);
                    daoFactory.intEntIdentifierDao().updateIntEntIdValue(id);
                } catch (RecordNotFoundException ex) {
                    throw new UnknownIntelectualEntity(entityId);
                }
            }
            // delete identifiers
            for (IntEntIdentifier id : plan.toDelete()) {
                try {
                    logger.log(Level.FINE, "deleting {0}", id);
                    daoFactory.intEntIdentifierDao().deleteIntEntIdentifier(id.getIntEntDbId(), id.getType());
                } catch (RecordNotFoundException ex) {
                    throw new UnknownIntelectualEntity(entityId);
                }
            }
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.Sigla;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigRepIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.DigitalRepresentation;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.DataAccessService;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author Martin Řehánek
 */
public class DataAccessServiceImpl extends BusinessServiceImpl implements DataAccessService {

    public DataAccessServiceImpl(DatabaseConnector con) {
        super(con);
    }

    @Override
    public UrnNbn urnByDigRepId(long digRepId) throws DatabaseException {
        try {
            return factory.urnDao().getUrnNbnByDigRegId(digRepId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    @Override
    public UrnNbnWithStatus urnBySiglaAndDocumentCode(String sigla, String documentCode) throws DatabaseException {
        try {
            UrnNbn urn = factory.urnDao().getUrnNbnByRegistrarCodeAndDocumentCode(sigla, documentCode);
            return new UrnNbnWithStatus(urn, UrnNbnWithStatus.Status.ACTIVE);
        } catch (RecordNotFoundException ex) { //urn:nb not in table urn:nbn
            try {
                UrnNbn urn = factory.urnReservedDao().getUrn(null, documentCode);
                //tmpMethodThrowsException();//todo: odstranit
                return new UrnNbnWithStatus(urn, UrnNbnWithStatus.Status.BOOKED);
            } catch (RecordNotFoundException ex2) { //urn:nbn also not booked
                try {
                    UrnNbn urn = null;//TODO: ziskat z tabulkyy opustenych
                    tmpMethodThrowsException();
                    //todo: remove tmpMethod and actually search
                    //in abandonedUrnNbn table
                    return new UrnNbnWithStatus(urn, UrnNbnWithStatus.Status.ABANDONED);
                } catch (RecordNotFoundException ex3) { //urn:nbn not even ebandoned
                    UrnNbn urn = new UrnNbn(sigla, documentCode, null);
                    return new UrnNbnWithStatus(urn, UrnNbnWithStatus.Status.FREE);
                }
            }
        }
    }

    @Override
    public DigitalRepresentation digRepByInternalId(long digRepId) throws DatabaseException {
        try {
            return factory.representationDao().getRepresentationByDbId(digRepId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    @Override
    public List<DigRepIdentifier> digRepIdentifiersByDigRepId(long id) throws DatabaseException {
        try {
            return factory.digRepIdDao().getIdList(id);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return Collections.<DigRepIdentifier>emptyList();
        }
    }

    @Override
    public List<DigitalInstance> instancesByDigRepId(long digRepId) throws DatabaseException {
        try {
            return factory.digInstDao().getDigitalInstancesOfDigRep(digRepId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return Collections.<DigitalInstance>emptyList();
        }
    }

    @Override
    public Registrar registrarById(long id) throws DatabaseException {
        try {
            return factory.registrarDao().getRegistrarById(id);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    @Override
    public Archiver archiverById(long id) throws DatabaseException {
        try {
            return factory.archiverDao().getArchiverById(id);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    @Override
    public IntelectualEntity entityById(long id) throws DatabaseException {
        try {
            return factory.intelectualEntityDao().getEntityByDbId(id);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    @Override
    public List<IntEntIdentifier> intEntIdentifiersByIntEntId(long intEntId) throws DatabaseException {
        try {
            return factory.intEntIdentifierDao().getIdList(intEntId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return Collections.<IntEntIdentifier>emptyList();
        }
    }

    @Override
    public Publication publicationByIntEntId(long intEntId) throws DatabaseException {
        try {
            return factory.publicationDao().getPublicationById(intEntId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    @Override
    public Originator originatorByIntEntId(long intEntId) throws DatabaseException {
        try {
            return factory.originatorDao().getOriginatorById(intEntId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    @Override
    public SourceDocument sourceDocumentByIntEntId(long intEntId) throws DatabaseException {
        try {
            return factory.srcDocDao().getSrcDocById(intEntId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    @Override
    public Registrar registrarBySigla(Sigla sigla) throws DatabaseException {
        try {
            return factory.registrarDao().getRegistrarBySigla(sigla);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    public List<DigitalLibrary> librariesByRegistrar(long registrarId) throws DatabaseException {
        try {
            return factory.digitalLibraryDao().getLibraries(registrarId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return Collections.<DigitalLibrary>emptyList();
        }
    }

    public List<Catalog> catalogsByRegistrar(long registrarId) throws DatabaseException {
        try {
            return factory.catalogDao().getCatalogs(registrarId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return Collections.<Catalog>emptyList();
        }
    }

    public List<Registrar> registrars() throws DatabaseException {
        return factory.registrarDao().getAllRegistrars();
    }

    public int digitalRepresentationsCount(long registrarId) throws DatabaseException {
        try {
            return factory.representationDao().getDigRepCount(registrarId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return 0;
        }
    }

    public DigitalRepresentation digRepByIdentifier(DigRepIdentifier id) throws DatabaseException {
        try {
            Long digRepId = factory.representationDao().getDigRepDbIdByIdentifier(id);
            return factory.representationDao().getRepresentationByDbId(digRepId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    private void tmpMethodThrowsException() throws RecordNotFoundException {
        if (true) {
            throw new RecordNotFoundException();
        }
    }

    public long digitalInstancesCount() throws DatabaseException {
        return factory.digInstDao().getTotalCount();
    }

    public DigitalInstance digInstanceByInternalId(long id) throws DatabaseException {
        try {
            return factory.digInstDao().getDigInstanceById(id);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }

    public DigitalLibrary libraryByInternalId(long libraryId) throws DatabaseException {
        try {
            return factory.digitalLibraryDao().getLibraryById(libraryId);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            return null;
        }
    }
}
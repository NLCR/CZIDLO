        /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services;

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
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import java.util.List;
import java.util.logging.Logger;

/**
 * TODO: zbavit se DatabaseException (resp. přeložit na RuntimeException)
 * @author Martin Řehánek
 */
public interface DataAccessService extends BusinessService {

    static final Logger logger = Logger.getLogger(DataAccessService.class.getName());

    public UrnNbnWithStatus urnBySiglaAndDocumentCode(Sigla sigla, String documentCode) throws DatabaseException;

    public DigitalRepresentation digRepByInternalId(long digRepId) throws DatabaseException;

    public UrnNbn urnByDigRepId(long id) throws DatabaseException;

    public List<DigRepIdentifier> digRepIdentifiersByDigRepId(long id) throws DatabaseException;

    public List<DigitalInstance> instancesByDigRepId(long digRepId) throws DatabaseException;

    public Registrar registrarById(long id) throws DatabaseException;

    public Archiver archiverById(long id) throws DatabaseException;

    public IntelectualEntity entityById(long id) throws DatabaseException;

    public List<IntEntIdentifier> intEntIdentifiersByIntEntId(long intEntId) throws DatabaseException;

    //pokud nenajde, vrati null
    public Publication publicationByIntEntId(long intEntId) throws DatabaseException;

    //pokud nenajde, vrati null
    public Originator originatorByIntEntId(long intEntId) throws DatabaseException;

    //pokud nenajde, vrati null
    public SourceDocument sourceDocumentByIntEntId(long intEntId) throws DatabaseException;

    public Registrar registrarBySigla(Sigla sigla) throws DatabaseException;

    public List<DigitalLibrary> librariesByRegistrar(long registrarId) throws DatabaseException;

    public List<Catalog> catalogsByRegistrar(long registrarId) throws DatabaseException;

    public List<Registrar> registrars() throws DatabaseException;

    public int digitalRepresentationsCount(long registrarId) throws DatabaseException;

    public DigitalRepresentation digRepByIdentifier(DigRepIdentifier id) throws DatabaseException;

    public long digitalInstancesCount() throws DatabaseException;

    public DigitalInstance digInstanceByInternalId(long id) throws DatabaseException;

    public DigitalLibrary libraryByInternalId(long libraryId) throws DatabaseException;
}

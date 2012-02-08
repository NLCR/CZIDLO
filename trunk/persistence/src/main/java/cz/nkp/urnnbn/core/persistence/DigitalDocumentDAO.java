/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public interface DigitalDocumentDAO {

    public String TABLE_NAME = "DigitalDocument";
    //TODO: prejmenovat jeste sekvenci
    public String SEQ_NAME = "seq_DigitalRepresentation";
    public String ATTR_ID = "id";
    public String ATTR_INT_ENT_ID = "intelectualEntityId";
    public String ATTR_REGISTRAR_ID = "registrarId";
    public String ATTR_ARCHIVER_ID = "archiverId";
    public String ATTR_CREATED = "created";
    public String ATTR_UPDATED = "lastUpdated";
    public String ATTR_EXTENT = "extent";
    public String ATTR_RESOLUTION = "resolution";
    public String ATTR_COLOR_DEPTH = "colorDepth";
    public String ATTR_FINANCED = "financedFrom";
    public String ATTR_CONTRACT_NUMBER = "contractNumber";

    /**
     * 
     * @param representation
     * @return
     * @throws DatabaseException
     * @throws RecordNotFoundException if registrar, archiver or intelectual entity not found
     */
    public Long insertRepresentation(DigitalDocument representation) throws DatabaseException, RecordNotFoundException;

    public DigitalDocument getRepresentationByDbId(long dbId) throws DatabaseException, RecordNotFoundException;

    //vyhledavani dig.rep. podle urn takto:
    //z UrnNbnDao ziskame id digRep a pak pres nej vytahnem data digRep
    public void updateRepresentation(DigitalDocument representation) throws DatabaseException, RecordNotFoundException;

    //TODO: vyhledavani podle jednoznacneho identifikatoru v ramci registratora
    //pozor: RecordNotFoundException muze byt jak pro registrarora, tak pro samotnou DR
    //mohly bych to oddelit tak, ze pokud nenajde digRep, vrati null
    public Long getDigRepDbIdByIdentifier(DigDocIdentifier id) throws DatabaseException, RecordNotFoundException;

    public Integer getDigRepCount(long registrarId) throws RecordNotFoundException, DatabaseException;

    public List<DigitalDocument> getRepresentationsOfIntEntity(long entityId) throws DatabaseException, RecordNotFoundException;

    public void deleteRepresentation(long digRepDbId) throws DatabaseException, RecordNotFoundException;

    public void deleteAllRepresentations() throws DatabaseException;
    //TODO: implementovat, pokud bude potreba
    //public List<Long> getRepresentationsDbIdByIdentifier(DigRepIdType type, String idValue) throws DatabaseException;
    //public List<Long> getAllRepresentationsId() throws DatabaseException;
    //public List<Long> getRepresentationsId(DigRepType type) throws DatabaseException;
    //public Long getRepresentationsCount() throws DatabaseException;
    //public Long getRepresentationsCount(DigRepType type) throws DatabaseException;
}

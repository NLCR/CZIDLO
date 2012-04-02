/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public interface DigitalDocumentDAO {

    public String TABLE_NAME = "DigitalDocument";
    public String SEQ_NAME = "seq_DigitalDocument";
    public String ATTR_ID = "id";
    public String ATTR_INT_ENT_ID = "intelectualEntityId";
    public String ATTR_REGISTRAR_ID = "registrarId";
    public String ATTR_ARCHIVER_ID = "archiverId";
    //
    public String ATTR_CREATED = "created";
    public String ATTR_UPDATED = "lastUpdated";
    //TODO: updated/created by USER_ID
    //
    public String ATTR_EXTENT = "extent";
    public String ATTR_FINANCED = "financedFrom";
    public String ATTR_CONTRACT_NUMBER = "contractNumber";
    //
    public String ATTR_FORMAT = "format";
    public String ATTR_FORMAT_VERSION = "formatVersion";
    public String ATTR_RES_HORIZONTAL = "resolutionHorizontal";
    public String ATTR_RES_VERTICAL = "resolutionVertical";
    public String ATTR_COMPRESSION = "compression";
    public String ATTR_COMPRESSION_RATIO = "compressionRatio";
    public String ATTR_COLOR_MODEL = "colorModel";
    public String ATTR_COLOR_DEPTH = "colorDepth";
    public String ATTR_ICC_PROFILE = "iccProfile";
    public String ATTR_PIC_WIDTH = "pictureWidth";
    public String ATTR_PIC_HEIGHT = "pictureHeight";

    /**
     * 
     * @param representation
     * @return
     * @throws DatabaseException
     * @throws RecordNotFoundException if registrar, archiver or intelectual entity not found
     */
    public Long insertDocument(DigitalDocument representation) throws DatabaseException, RecordNotFoundException;

    public DigitalDocument getDocumentByDbId(long dbId) throws DatabaseException, RecordNotFoundException;

    //vyhledavani dig.rep. podle urn takto:
    //z UrnNbnDao ziskame id digRep a pak pres nej vytahnem data digRep
    public void updateDocument(DigitalDocument document) throws DatabaseException, RecordNotFoundException;

    //TODO: vyhledavani podle jednoznacneho identifikatoru v ramci registratora
    //pozor: RecordNotFoundException muze byt jak pro registrarora, tak pro samotnou DR
    //mohly bych to oddelit tak, ze pokud nenajde digRep, vrati null
    public Long getDigDocDbIdByIdentifier(DigDocIdentifier id) throws DatabaseException, RecordNotFoundException;

    public Integer getDigDocCount(long registrarId) throws RecordNotFoundException, DatabaseException;

    public List<DigitalDocument> getDocumentsOfIntEntity(long entityId) throws DatabaseException, RecordNotFoundException;

    public void deleteDocument(long digRepDbId) throws DatabaseException, RecordNotFoundException, RecordReferencedException;

    public void deleteAllDocuments() throws DatabaseException, RecordReferencedException;
    //TODO: implementovat, pokud bude potreba
    //public List<Long> getRepresentationsDbIdByIdentifier(DigRepIdType type, String idValue) throws DatabaseException;
    //public List<Long> getAllRepresentationsId() throws DatabaseException;
    //public List<Long> getRepresentationsId(DigRepType type) throws DatabaseException;
    //public Long getRepresentationsCount() throws DatabaseException;
    //public Long getRepresentationsCount(DigRepType type) throws DatabaseException;
}

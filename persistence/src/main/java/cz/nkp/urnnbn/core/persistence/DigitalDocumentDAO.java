/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import java.util.List;

import org.joda.time.DateTime;

import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;

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
    public String ATTR_UPDATED = "modified";
    // TODO: updated/created by USER_ID
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
     * @throws RecordNotFoundException
     *             if registrar, archiver or intelectual entity not found
     */
    public Long insertDocument(DigitalDocument doc) throws DatabaseException, RecordNotFoundException;

    public DigitalDocument getDocumentByDbId(long dbId) throws DatabaseException, RecordNotFoundException;

    /**
     *
     * @param id
     * @return
     * @throws DatabaseException
     * @throws RecordNotFoundException
     *             if registrar or digital document is doesn't exist
     */
    public Long getDigDocIdByRegistrarScopeId(RegistrarScopeIdentifier id) throws DatabaseException, RecordNotFoundException;

    public Integer getDigDocCount(long registrarId) throws RecordNotFoundException, DatabaseException;

    public List<DigitalDocument> getDocumentsOfIntEntity(long entityId) throws DatabaseException, RecordNotFoundException;

    /**
     *
     * @param registrarId
     * @param from
     * @param until
     * @return
     * @throws DatabaseException
     * @throws RecordNotFoundException
     *             if registrar doesn't exist
     */
    public List<DigitalDocument> getDigDocsByRegistrarIdAndTimestamps(long registrarId, DateTime from, DateTime until) throws DatabaseException,
            RecordNotFoundException;

    public List<DigitalDocument> getDigDocsByTimestamps(DateTime from, DateTime until) throws DatabaseException;

    public List<Long> getDigDocDbIdListByTimestamps(DateTime from, DateTime until) throws DatabaseException;

    /**
     *
     * @param document
     *            digital document to be updated
     * @throws DatabaseException
     * @throws RecordNotFoundException
     *             if digital document with id obtained from document doesn't exist
     */
    public void updateDocument(DigitalDocument document) throws DatabaseException, RecordNotFoundException;

    public void updateDocumentDatestamp(Long digDocId) throws DatabaseException, RecordNotFoundException;

    public void deleteDocument(long digitalDocumentId) throws DatabaseException, RecordNotFoundException, RecordReferencedException;

    public void deleteAllDocuments() throws DatabaseException, RecordReferencedException;
}

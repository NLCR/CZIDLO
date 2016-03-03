/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import java.util.List;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;

/**
 *
 * @author Martin Řehánek
 */
public interface ArchiverDAO {

    public String TABLE_NAME = "Archiver";
    public String SEQ_NAME = "seq_Archiver";
    public String ATTR_ID = "id";
    public String ATTR_CREATED = "created";
    public String ATTR_UPDATED = "modified";
    public String ATTR_NAME = "name";
    public String ATTR_DESCRIPTION = "description";
    public String ATTR_ORDER = "item_order";
    public String ATTR_HIDDEN = "hidden";

    /**
     * Should not use id from archiver but instead create new by means of database
     * 
     * @param archiver
     * @return id created
     * @throws DatabaseException
     */
    public Long insertArchiver(Archiver archiver) throws DatabaseException;

    public Archiver getArchiverById(long id) throws DatabaseException, RecordNotFoundException;

    public List<Archiver> getAllArchivers() throws DatabaseException;

    public List<Long> getAllArchiversId() throws DatabaseException;

    public void updateArchiver(Archiver archiver) throws DatabaseException, RecordNotFoundException;

    public void deleteArchiver(long id) throws DatabaseException, RecordNotFoundException, RecordReferencedException;

    public void deleteAllArchivers() throws DatabaseException, RecordReferencedException;
}

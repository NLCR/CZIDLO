/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.persistence.ArchiverDAO;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.DigitalDocumentDAO;
import cz.nkp.urnnbn.core.persistence.IntelectualEntityDAO;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import cz.nkp.urnnbn.core.persistence.RegistrarScopeIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.operations.DaoOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.MultipleResultsOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.OperationUtils;
import cz.nkp.urnnbn.core.persistence.impl.operations.SingleResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.postgres.statements.SelectNewIdFromSequence;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertDigitalDocument;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByLongAttr;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByTimestamps;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsbyTimestampsLong;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectCountByLong;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectSingleAttrByLongStringString;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectSingleAttrByTimestamps;
import cz.nkp.urnnbn.core.persistence.impl.statements.UpdateDigitalDocument;
import cz.nkp.urnnbn.core.persistence.impl.transformations.DigitalDocumentRT;
import cz.nkp.urnnbn.core.persistence.impl.transformations.SingleIntRT;
import cz.nkp.urnnbn.core.persistence.impl.transformations.singleLongRT;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;

/**
 * 
 * @author Martin Řehánek
 */
public class DigitalDocumentDaoPostgres extends AbstractDAO implements DigitalDocumentDAO {

	private static final Logger logger = Logger.getLogger(DigitalDocumentDaoPostgres.class.getName());

	public DigitalDocumentDaoPostgres(DatabaseConnector con) {
		super(con);
	}

	@Override
	public Long insertDocument(final DigitalDocument representation) throws DatabaseException, RecordNotFoundException {
		// TODO: melo by byt vsechno v transakci
		checkRecordExists(RegistrarDAO.TABLE_NAME, RegistrarDAO.ATTR_ID, representation.getRegistrarId());
		checkRecordExists(ArchiverDAO.TABLE_NAME, ArchiverDAO.ATTR_ID, representation.getArchiverId());
		checkRecordExists(IntelectualEntityDAO.TABLE_NAME, IntelectualEntityDAO.ATTR_ID, representation.getIntEntId());
		DaoOperation operation = new DaoOperation() {

			@Override
			public Object run(Connection connection) throws DatabaseException, SQLException {
				// get new id
				StatementWrapper newId = new SelectNewIdFromSequence(SEQ_NAME);
				PreparedStatement newIdSt = connection.prepareStatement(newId.preparedStatement());
				newId.populate(newIdSt);
				ResultSet idResultSet = newIdSt.executeQuery();
				Long id = OperationUtils.resultSet2Long(idResultSet);
				// set id
				representation.setId(id);
				// insert
				StatementWrapper insert = new InsertDigitalDocument(representation);
				PreparedStatement insertSt = OperationUtils.preparedStatementFromWrapper(connection, insert);
				insertSt.executeUpdate();
				return id;
			}
		};
		try {
			Long id = (Long) runInTransaction(operation);
			return id;
		} catch (PersistenceException ex) {
			// should never happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
			return null;
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
	public DigitalDocument getDocumentByDbId(long dbId) throws DatabaseException, RecordNotFoundException {
		return (DigitalDocument) getRecordById(TABLE_NAME, ATTR_ID, dbId, new DigitalDocumentRT());
	}

	@Override
	public Integer getDigDocCount(long registrarId) throws RecordNotFoundException, DatabaseException {
		checkRecordExists(RegistrarDAO.TABLE_NAME, RegistrarDAO.ATTR_ID, registrarId);
		StatementWrapper statement = new SelectCountByLong(TABLE_NAME, ATTR_REGISTRAR_ID, registrarId);
		DaoOperation operation = new SingleResultOperation(statement, new SingleIntRT());
		try {
			return (Integer) runInTransaction(operation);
		} catch (PersistenceException ex) {
			// should never happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
			return null;
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
    @SuppressWarnings("unchecked")
	public List<DigitalDocument> getDocumentsOfIntEntity(long entityId) throws DatabaseException, RecordNotFoundException {
		checkRecordExists(IntelectualEntityDAO.TABLE_NAME, IntelectualEntityDAO.ATTR_ID, entityId);
		try {
			StatementWrapper st = new SelectAllAttrsByLongAttr(TABLE_NAME, ATTR_INT_ENT_ID, entityId);
			DaoOperation operation = new MultipleResultsOperation(st, new DigitalDocumentRT());
			return (List<DigitalDocument>) runInTransaction(operation);
		} catch (PersistenceException ex) {
			// cannot happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
			return null;
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	// TODO: test
	@Override
    @SuppressWarnings("unchecked")
	public List<DigitalDocument> getDigDocsByRegistrarIdAndTimestamps(long registrarId, DateTime from, DateTime until)
			throws DatabaseException, RecordNotFoundException {
		checkRecordExists(RegistrarDAO.TABLE_NAME, RegistrarDAO.ATTR_ID, registrarId);
		try {
			StatementWrapper st = new SelectAllAttrsbyTimestampsLong(TABLE_NAME, ATTR_UPDATED, from, until, ATTR_REGISTRAR_ID, registrarId);
			DaoOperation operation = new MultipleResultsOperation(st, new DigitalDocumentRT());
			return (List<DigitalDocument>) runInTransaction(operation);
		} catch (PersistenceException ex) {
			// cannot happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
			return null;
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
    @SuppressWarnings("unchecked")
	public List<DigitalDocument> getDigDocsByTimestamps(DateTime from, DateTime until) throws DatabaseException {
		try {
			StatementWrapper st = new SelectAllAttrsByTimestamps(TABLE_NAME, ATTR_UPDATED, from, until);
			DaoOperation operation = new MultipleResultsOperation(st, new DigitalDocumentRT());
			return (List<DigitalDocument>) runInTransaction(operation);
		} catch (PersistenceException ex) {
			// cannot happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
			return null;
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
    @SuppressWarnings("unchecked")
	public List<Long> getDigDocDbIdListByTimestamps(DateTime from, DateTime until) throws DatabaseException {
		try {
			StatementWrapper st = new SelectSingleAttrByTimestamps(TABLE_NAME, ATTR_UPDATED, from, until, ATTR_ID);
			DaoOperation operation = new MultipleResultsOperation(st, new singleLongRT());
			return (List<Long>) runInTransaction(operation);
		} catch (PersistenceException ex) {
			// cannot happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
			return null;
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
	public Long getDigDocIdByRegistrarScopeId(RegistrarScopeIdentifier id) throws DatabaseException, RecordNotFoundException {
		StatementWrapper statement = new SelectSingleAttrByLongStringString(RegistrarScopeIdentifierDAO.TABLE_NAME,
				RegistrarScopeIdentifierDAO.ATTR_DIG_DOC_ID, RegistrarScopeIdentifierDAO.ATTR_REG_ID, id.getRegistrarId(),
				RegistrarScopeIdentifierDAO.ATTR_TYPE, id.getType().toString(), RegistrarScopeIdentifierDAO.ATTR_VALUE, id.getValue());
		DaoOperation operation = new SingleResultOperation(statement, new singleLongRT());
		try {
			return (Long) runInTransaction(operation);
		} catch (RecordNotFoundException e) {
			// throw e;
			throw new RecordNotFoundException(id.toString());
		} catch (PersistenceException ex) {
			// should never happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
			return null;
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
	public void updateDocument(DigitalDocument document) throws DatabaseException, RecordNotFoundException {
		checkRecordExists(RegistrarDAO.TABLE_NAME, RegistrarDAO.ATTR_ID, document.getRegistrarId());
		checkRecordExists(IntelectualEntityDAO.TABLE_NAME, IntelectualEntityDAO.ATTR_ID, document.getIntEntId());
		updateRecordWithLongPK(document, TABLE_NAME, ATTR_ID, new UpdateDigitalDocument(document));
	}

	@Override
	public void updateDocumentDatestamp(Long digDocId) throws DatabaseException, RecordNotFoundException {
		updateRecordTimestamp(TABLE_NAME, ATTR_ID, digDocId, ATTR_UPDATED);
	}

	@Override
	public void deleteDocument(long digDocId) throws DatabaseException, RecordNotFoundException, RecordReferencedException {
		deleteRecordsById(TABLE_NAME, ATTR_ID, digDocId, true);
	}

	@Override
	public void deleteAllDocuments() throws DatabaseException, RecordReferencedException {
		deleteAllRecords(DigitalDocumentDAO.TABLE_NAME);
	}
}

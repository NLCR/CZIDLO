/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.UrnNbnExport;
import cz.nkp.urnnbn.core.UrnNbnExportFilter;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.UrnNbnWithStatus.Status;
import cz.nkp.urnnbn.core.dto.Statistic;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.DigitalDocumentDAO;
import cz.nkp.urnnbn.core.persistence.UrnNbnDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.IdPart;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.operations.DaoOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.MultipleResultsOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.NoResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.SingleResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.postgres.statements.SelectStatistics;
import cz.nkp.urnnbn.core.persistence.impl.statements.DeactivateUrnNbn;
import cz.nkp.urnnbn.core.persistence.impl.statements.DeleteByStringString;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertUrnNbn;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertUrnNbnPredecessor;
import cz.nkp.urnnbn.core.persistence.impl.statements.ReactivateUrnNbn;
import cz.nkp.urnnbn.core.persistence.impl.statements.Select3StringsBy2Strings;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByStringAttr;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByStringString;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByTimestamps;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsbyTimestampsString;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectCountBy4Strings;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectMinDateStatement;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectUrnNbnExport;
import cz.nkp.urnnbn.core.persistence.impl.transformations.SingleLongRT;
import cz.nkp.urnnbn.core.persistence.impl.transformations.SingleTimestampRT;
import cz.nkp.urnnbn.core.persistence.impl.transformations.StatisticsRT;
import cz.nkp.urnnbn.core.persistence.impl.transformations.ThreeStringsRT;
import cz.nkp.urnnbn.core.persistence.impl.transformations.UrnNbnExportRT;
import cz.nkp.urnnbn.core.persistence.impl.transformations.UrnNbnRT;

/**
 * 
 * @author Martin Řehánek
 */
public class UrnNbnDaoPostgres extends AbstractDAO implements UrnNbnDAO {

	private static final Logger logger = Logger.getLogger(UrnNbnDaoPostgres.class.getName());

	public UrnNbnDaoPostgres(DatabaseConnector con) {
		super(con);
	}

	@Override
	public void insertUrnNbn(UrnNbn urn) throws DatabaseException, AlreadyPresentException, RecordNotFoundException {
		checkRecordExists(DigitalDocumentDAO.TABLE_NAME, DigitalDocumentDAO.ATTR_ID, urn.getDigDocId());
		StatementWrapper st = new InsertUrnNbn(urn);
		DaoOperation operation = new NoResultOperation(st);
		try {
			runInTransaction(operation);
		} catch (PersistenceException ex) {
			// should never happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
		} catch (SQLException ex) {
			if ("23505".equals(ex.getSQLState())) {
				IdPart digDocId = new IdPart(ATTR_DIG_DOC_ID, Long.toString(urn.getDigDocId()));
				IdPart registrarCode = new IdPart(ATTR_REGISTRAR_CODE, urn.getRegistrarCode().toString());
				IdPart documentCode = new IdPart(ATTR_DOCUMENT_CODE, urn.getDocumentCode());
				throw new AlreadyPresentException(new IdPart[] { digDocId, registrarCode, documentCode });
			} else {
				throw new DatabaseException(ex);
			}
		}
	}

	@Override
	public void insertUrnNbnPredecessor(UrnNbn predecessor, UrnNbn successor, String note) throws DatabaseException, AlreadyPresentException,
			RecordNotFoundException {
		checkRecordExists(UrnNbnDAO.TABLE_NAME, UrnNbnDAO.ATTR_REGISTRAR_CODE, predecessor.getRegistrarCode().toString(),
				UrnNbnDAO.ATTR_DOCUMENT_CODE, predecessor.getDocumentCode());
		checkRecordExists(UrnNbnDAO.TABLE_NAME, UrnNbnDAO.ATTR_REGISTRAR_CODE, successor.getRegistrarCode().toString(), UrnNbnDAO.ATTR_DOCUMENT_CODE,
				successor.getDocumentCode());
		if (isPredecessesor(predecessor, successor)) {
			throw new AlreadyPresentException(null);
		}
		StatementWrapper st = new InsertUrnNbnPredecessor(predecessor, successor, note);
		DaoOperation operation = new NoResultOperation(st);
		try {
			runInTransaction(operation);
		} catch (PersistenceException ex) {
			// should never happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
		} catch (SQLException ex) {
			if ("23505".equals(ex.getSQLState())) {
				throw new AlreadyPresentException(null);
			} else {
				throw new DatabaseException(ex);
			}
		}
	}

	@Override
	public UrnNbn getUrnNbnByDigDocId(Long digDocId) throws DatabaseException, RecordNotFoundException {
		return (UrnNbn) getRecordById(TABLE_NAME, ATTR_DIG_DOC_ID, digDocId, new UrnNbnRT());
	}

	@Override
	public UrnNbn getUrnNbnByRegistrarCodeAndDocumentCode(RegistrarCode registrarCode, String documentCode) throws DatabaseException,
			RecordNotFoundException {
		StatementWrapper wrapper = new SelectAllAttrsByStringString(TABLE_NAME, ATTR_REGISTRAR_CODE, registrarCode.toString(), ATTR_DOCUMENT_CODE,
				documentCode);
		DaoOperation operation = new SingleResultOperation(wrapper, new UrnNbnRT());
		try {
			return (UrnNbn) runInTransaction(operation);
		} catch (RecordNotFoundException e) {
			logger.log(Level.INFO, "No such urn:nbn with registrar code {0} and document code {1}", new Object[] { registrarCode, documentCode });
			throw (RecordNotFoundException) e;
		} catch (PersistenceException e) {
			// should never happen
			logger.log(Level.SEVERE, "Exception unexpected here", e);
			return null;
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
	public List<UrnNbn> getUrnNbnsByRegistrarCode(RegistrarCode registrarCode) throws DatabaseException {
		StatementWrapper wrapper = new SelectAllAttrsByStringAttr(TABLE_NAME, ATTR_REGISTRAR_CODE, registrarCode.toString());
		DaoOperation operation = new MultipleResultsOperation(wrapper, new UrnNbnRT());
		try {
			return (List<UrnNbn>) runInTransaction(operation);
		} catch (PersistenceException e) {
			// should never happen
			logger.log(Level.SEVERE, "Exception unexpected here", e);
			return null;
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	// TODO: test
	@Override
	public List<UrnNbn> getUrnNbnsByTimestamps(DateTime from, DateTime until) throws DatabaseException {
		try {
			StatementWrapper st = new SelectAllAttrsByTimestamps(TABLE_NAME, ATTR_DEACTIVATED, from, until);
			DaoOperation operation = new MultipleResultsOperation(st, new UrnNbnRT());
			return (List<UrnNbn>) runInTransaction(operation);
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
	public List<UrnNbn> getUrnNbnsByRegistrarCodeAndTimestamps(RegistrarCode registrarCode, DateTime from, DateTime until) throws DatabaseException {
		try {
			StatementWrapper st = new SelectAllAttrsbyTimestampsString(TABLE_NAME, ATTR_DEACTIVATED, from, until, ATTR_REGISTRAR_CODE,
					registrarCode.toString());
			DaoOperation operation = new MultipleResultsOperation(st, new UrnNbnRT());
			return (List<UrnNbn>) runInTransaction(operation);
		} catch (PersistenceException ex) {
			// cannot happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
			return null;
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
	public List<UrnNbnWithStatus> getPredecessors(UrnNbn successor) throws DatabaseException {
		try {
			StatementWrapper st = new Select3StringsBy2Strings(SUCCESSOR_TABLE_NAME, ATTR_SUCCESSOR_REGISTRAR_CODE, successor.getRegistrarCode()
					.toString(), ATTR_SUCCESSOR_DOCUMENT_CODE, successor.getDocumentCode(), ATTR_PRECESSOR_REGISTRAR_CODE,
					ATTR_PRECESSOR_DOCUMENT_CODE, ATTR_NOTE);
			DaoOperation operation = new MultipleResultsOperation(st, new ThreeStringsRT());
			List<String[]> predecessorsData = (List<String[]>) runInTransaction(operation);
			return urnNbnListFromCodes(predecessorsData);
		} catch (PersistenceException ex) {
			// cannot happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
			return Collections.<UrnNbnWithStatus> emptyList();
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
	public List<UrnNbnWithStatus> getSuccessors(UrnNbn predecessor) throws DatabaseException {
		try {
			StatementWrapper st = new Select3StringsBy2Strings(SUCCESSOR_TABLE_NAME, ATTR_PRECESSOR_REGISTRAR_CODE, predecessor.getRegistrarCode()
					.toString(), ATTR_PRECESSOR_DOCUMENT_CODE, predecessor.getDocumentCode(), ATTR_SUCCESSOR_REGISTRAR_CODE,
					ATTR_SUCCESSOR_DOCUMENT_CODE, ATTR_NOTE);
			DaoOperation operation = new MultipleResultsOperation(st, new ThreeStringsRT());
			List<String[]> successorsData = (List<String[]>) runInTransaction(operation);
			return urnNbnListFromCodes(successorsData);
		} catch (PersistenceException ex) {
			// cannot happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
			return Collections.<UrnNbnWithStatus> emptyList();
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	private List<UrnNbnWithStatus> urnNbnListFromCodes(List<String[]> urnNbnDataList) {
		List<UrnNbnWithStatus> result = new ArrayList<UrnNbnWithStatus>(urnNbnDataList.size());
		for (String[] urnNbnData : urnNbnDataList) {
			RegistrarCode registrarCode = RegistrarCode.valueOf(urnNbnData[0]);
			String documentCode = urnNbnData[1];
			String note = urnNbnData[2];
			try {
				UrnNbn urnNbn = getUrnNbnByRegistrarCodeAndDocumentCode(registrarCode, documentCode);
				Status status = urnNbn.isActive() ? UrnNbnWithStatus.Status.ACTIVE : UrnNbnWithStatus.Status.DEACTIVATED;
				UrnNbnWithStatus withStatus = new UrnNbnWithStatus(urnNbn, status, note);
				result.add(withStatus);
			} catch (DatabaseException ex) {
				Logger.getLogger(UrnNbnDaoPostgres.class.getName()).log(Level.SEVERE, null, ex);
			} catch (RecordNotFoundException ex) {
				Logger.getLogger(UrnNbnDaoPostgres.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return result;
	}

	@Override
	public boolean isPredecessesor(UrnNbn precessor, UrnNbn successor) throws DatabaseException {
		StatementWrapper st = new SelectCountBy4Strings(SUCCESSOR_TABLE_NAME, ATTR_PRECESSOR_REGISTRAR_CODE, precessor.getRegistrarCode().toString(),
				ATTR_PRECESSOR_DOCUMENT_CODE, precessor.getDocumentCode(), ATTR_SUCCESSOR_REGISTRAR_CODE, successor.getRegistrarCode().toString(),
				ATTR_SUCCESSOR_DOCUMENT_CODE, successor.getDocumentCode());
		DaoOperation op = new SingleResultOperation(st, new SingleLongRT());
		try {
			Long records = (Long) runInTransaction(op);
			return records > 0;
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		} catch (PersistenceException ex) {
			// should never happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
			return false;
		}
	}

	@Override
	public void deactivateUrnNbn(RegistrarCode registrarCode, String documentCode, String note) throws DatabaseException {
		DaoOperation operation = new NoResultOperation(new DeactivateUrnNbn(registrarCode, documentCode, note));
		try {
			runInTransaction(operation);
		} catch (PersistenceException e) {
			// should never happen
			logger.log(Level.SEVERE, "Exception unexpected here", e);
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
	public void reactivateUrnNbn(RegistrarCode registrarCode, String documentCode) throws DatabaseException {
		DaoOperation operation = new NoResultOperation(new ReactivateUrnNbn(registrarCode, documentCode));
		try {
			runInTransaction(operation);
		} catch (PersistenceException e) {
			// should never happen
			logger.log(Level.SEVERE, "Exception unexpected here", e);
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
	public void deleteUrnNbn(RegistrarCode registrarCode, String documentCode) throws DatabaseException {
		StatementWrapper wrapper = new DeleteByStringString(TABLE_NAME, ATTR_REGISTRAR_CODE, registrarCode.toString(), ATTR_DOCUMENT_CODE,
				documentCode);
		DaoOperation operation = new NoResultOperation(wrapper);
		try {
			runInTransaction(operation);
		} catch (PersistenceException e) {
			// should never happen
			logger.log(Level.SEVERE, "Exception unexpected here", e);
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
	public void deleteAllUrnNbns() throws DatabaseException {
		try {
			deleteAllRecords(TABLE_NAME);
		} catch (RecordReferencedException ex) {
			// should never happen
			logger.log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void deletePredecessors(UrnNbn urn) throws DatabaseException {
		try {
			final StatementWrapper st = new DeleteByStringString(SUCCESSOR_TABLE_NAME, ATTR_SUCCESSOR_REGISTRAR_CODE, urn.getRegistrarCode()
					.toString(), ATTR_SUCCESSOR_DOCUMENT_CODE, urn.getDocumentCode());
			DaoOperation operation = new NoResultOperation(st);
			runInTransaction(operation);
		} catch (PersistenceException ex) {
			// cannot happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "Cannot delete predecessors of {0}", urn);
			throw new DatabaseException(ex);
		}
	}

	@Override
	public void deleteSuccessors(UrnNbn urn) throws DatabaseException {
		try {
			final StatementWrapper st = new DeleteByStringString(SUCCESSOR_TABLE_NAME, ATTR_PRECESSOR_REGISTRAR_CODE, urn.getRegistrarCode()
					.toString(), ATTR_PRECESSOR_DOCUMENT_CODE, urn.getDocumentCode());
			DaoOperation operation = new NoResultOperation(st);
			runInTransaction(operation);
		} catch (PersistenceException ex) {
			// cannot happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
		} catch (SQLException ex) {
			logger.log(Level.SEVERE, "Cannot delete successors of {0}", urn);
			throw new DatabaseException(ex);
		}
	}

	@Override
	public void deleteAllPredecessors() throws DatabaseException {
		try {
			deleteAllRecords(SUCCESSOR_TABLE_NAME);
		} catch (RecordReferencedException ex) {
			// should never happen
			logger.log(Level.SEVERE, null, ex);
		}
	}

	// TODO: otestovat
	@Override
	@SuppressWarnings("unchecked")
	public List<UrnNbnExport> selectByCriteria(String languageCode, UrnNbnExportFilter filter, boolean withDigitalInstances) throws DatabaseException {
		try {
			SelectUrnNbnExport st = new SelectUrnNbnExport(languageCode, filter, withDigitalInstances);
			DaoOperation operation = new MultipleResultsOperation(st, new UrnNbnExportRT());
			return (List<UrnNbnExport>) runInTransaction(operation);
		} catch (PersistenceException ex) {
			// cannot happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
			return Collections.<UrnNbnExport> emptyList();
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
	public Integer getAssignmentsFirstYear() throws DatabaseException {
		try {
			SelectMinDateStatement statement = new SelectMinDateStatement(UrnNbnDAO.TABLE_NAME, UrnNbnDAO.ATTR_REGISTERED);
			DaoOperation operation = new SingleResultOperation(statement, new SingleTimestampRT());
			Timestamp timeStamp = (Timestamp) runInTransaction(operation);
			if (timeStamp != null) {
				return Integer.valueOf(timeStamp.getYear() + 1900);
			} else {
				return null;
			}
		} catch (PersistenceException ex) {
			logger.severe(ex.getMessage());
			throw new DatabaseException(ex);
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
	public List<Statistic> getUrnNbnAssignmentStatistics(String registrarCode, boolean includeActive, boolean includeDeactivated)
			throws DatabaseException {
		try {
			String registarCodeStr = registrarCode == null ? null : registrarCode.toString();
			SelectStatistics st = new SelectStatistics(UrnNbnDAO.TABLE_NAME, UrnNbnDAO.ATTR_REGISTRAR_CODE, UrnNbnDAO.ATTR_REGISTERED,
					UrnNbnDAO.ATTR_ACTIVE, registarCodeStr, includeActive, includeDeactivated);
			DaoOperation operation = new MultipleResultsOperation(st, new StatisticsRT());
			return (List<Statistic>) runInTransaction(operation);
		} catch (PersistenceException ex) {
			// cannot happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
			return Collections.<Statistic> emptyList();
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	public List<Statistic> getUrnNbnRegistrationStatistics(boolean includeActive, boolean includeDeactivated) throws DatabaseException {
		return getUrnNbnAssignmentStatistics(null, includeActive, includeDeactivated);
	}

}

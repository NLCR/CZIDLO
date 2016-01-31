/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import cz.nkp.urnnbn.core.persistence.impl.postgres.statements.SelectNewIdFromSequence;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.impl.operations.DaoOperation;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import cz.nkp.urnnbn.core.persistence.UserDAO;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.operations.OperationUtils;
import cz.nkp.urnnbn.core.persistence.impl.operations.MultipleResultsOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.NoResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.SingleResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertUser;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertUserRegistrar;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByStringAttr;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectSingleAttrByLong;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectSingleAttrByString;
import cz.nkp.urnnbn.core.persistence.impl.statements.UpdateUser;
import cz.nkp.urnnbn.core.persistence.impl.transformations.SingleLongRT;
import cz.nkp.urnnbn.core.persistence.impl.transformations.UserRT;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Martin Řehánek
 */
public class UserDaoPostgres extends AbstractDAO implements UserDAO {

	private static final Logger logger = Logger.getLogger(UserDaoPostgres.class.getName());

	public UserDaoPostgres(DatabaseConnector con) {
		super(con);
	}

	@Override
	public Long insertUser(final User user) throws DatabaseException, AlreadyPresentException {
		DaoOperation operation = new DaoOperation() {

			@Override
			public Object run(Connection connection) throws DatabaseException, SQLException, AlreadyPresentException {
				// find user with same login
				StatementWrapper usersByLogin = new SelectSingleAttrByString(TABLE_NAME, ATTR_LOGIN, user.getLogin(), ATTR_ID);
				PreparedStatement usersByLoginSt = OperationUtils.preparedStatementFromWrapper(connection, usersByLogin);
				ResultSet presentIdResultSet = usersByLoginSt.executeQuery();
				Long presentId = OperationUtils.resultSet2Long(presentIdResultSet);
				if (presentId != null) {
					throw new AlreadyPresentException(presentId);
				}

				// get new id
				StatementWrapper newId = new SelectNewIdFromSequence(SEQ_NAME);
				PreparedStatement newIdSt = OperationUtils.preparedStatementFromWrapper(connection, newId);
				ResultSet idResultSet = newIdSt.executeQuery();
				Long id = OperationUtils.resultSet2Long(idResultSet);
				// insert with id
				user.setId(id);
				StatementWrapper insert = new InsertUser(user);
				PreparedStatement insertSt = OperationUtils.preparedStatementFromWrapper(connection, insert);
				insertSt.executeUpdate();
				return id;
			}
		};
		try {
			Long id = (Long) runInTransaction(operation);
			return id;
		} catch (AlreadyPresentException ex) {
			throw ex;
		} catch (PersistenceException ex) {
			// should never happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
			return null;
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	public void insertAdministrationRight(long registrarId, long userId) throws DatabaseException, RecordNotFoundException,
			AlreadyPresentException {
		checkRecordExists(TABLE_NAME, ATTR_ID, userId);
		checkRecordExists(RegistrarDAO.TABLE_NAME, RegistrarDAO.ATTR_ID, registrarId);
		try {
			StatementWrapper statement = new InsertUserRegistrar(userId, registrarId);
			DaoOperation operation = new NoResultOperation(statement);
			runInTransaction(operation);
		} catch (PersistenceException ex) {
			// should never happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
			return;
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
	public User getUserById(long id) throws DatabaseException, RecordNotFoundException {
		return (User) getRecordById(TABLE_NAME, ATTR_ID, id, new UserRT());
	}

	@Override
	public User getUserByLogin(String login) throws DatabaseException, RecordNotFoundException {
		try {
			StatementWrapper st = new SelectAllAttrsByStringAttr(TABLE_NAME, ATTR_LOGIN, login);
			DaoOperation operation = new SingleResultOperation(st, new UserRT());
			return (User) runInTransaction(operation);
		} catch (RecordNotFoundException ex) {
			throw ex;
		} catch (PersistenceException ex) {
			// should never happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
			return null;
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
	public List<Long> getAdminsOfRegistrar(long registrarId) throws DatabaseException, RecordNotFoundException {
		checkRecordExists(RegistrarDAO.TABLE_NAME, RegistrarDAO.ATTR_ID, registrarId);
		try {
			StatementWrapper st = new SelectSingleAttrByLong(TABLE_USER_REGISTRAR_NAME, USER_REGISTRAR_ATTR_REGISTRAR_ID, registrarId,
					USER_REGISTRAR_ATTR_USER_ID);
			DaoOperation operation = new MultipleResultsOperation(st, new SingleLongRT());
			return (List<Long>) runInTransaction(operation);
		} catch (RecordNotFoundException ex) {
			throw ex;
		} catch (PersistenceException ex) {
			// should never happen
			logger.log(Level.SEVERE, "Exception unexpected here", ex);
			return null;
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
	public List<Long> getAllUsersId() throws DatabaseException {
		return getIdListOfAllRecords(TABLE_NAME, ATTR_ID);
	}

	public List<User> getAllUsers() throws DatabaseException {
		return (List<User>) getAllRecords(TABLE_NAME, new UserRT());
	}

	@Override
	public void updateUser(User user) throws DatabaseException, RecordNotFoundException {
		updateRecordWithLongPK(user, TABLE_NAME, ATTR_ID, new UpdateUser(user));
	}

	@Override
	public void deleteUser(long id) throws DatabaseException, RecordNotFoundException {
		try {
			deleteRecordsById(TABLE_NAME, ATTR_ID, id, true);
		} catch (RecordReferencedException ex) {
			// should never happen
			logger.log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void deleteAllUsers() throws DatabaseException {
		try {
			deleteAllRecords(TABLE_NAME);
		} catch (RecordReferencedException ex) {
			// should never happen
			logger.log(Level.SEVERE, null, ex);
		}
	}

	public void deleteAdministrationRight(long registarId, long userId) throws DatabaseException, RecordNotFoundException {
		try {
			checkRecordExists(TABLE_NAME, ATTR_ID, userId);
			checkRecordExists(RegistrarDAO.TABLE_NAME, RegistrarDAO.ATTR_ID, registarId);
			deleteRecordsByLongAndLong(TABLE_USER_REGISTRAR_NAME, USER_REGISTRAR_ATTR_REGISTRAR_ID, registarId,
					USER_REGISTRAR_ATTR_USER_ID, userId);
		} catch (RecordReferencedException ex) {
			// cannot happen
			logger.log(Level.SEVERE, null, ex);
		}
	}
}

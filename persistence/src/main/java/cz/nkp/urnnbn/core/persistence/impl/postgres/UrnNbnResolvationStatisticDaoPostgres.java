package cz.nkp.urnnbn.core.persistence.impl.postgres;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.nkp.urnnbn.core.dto.Statistic;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.UrnNbnResolvationStatisticDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.IdPart;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.operations.DaoOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.MultipleResultsOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.NoResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.SingleResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrs;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByStringAttr;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByStringIntInt;
import cz.nkp.urnnbn.core.persistence.impl.transformations.UrnNbnResolvationStatisticRT;

public class UrnNbnResolvationStatisticDaoPostgres extends AbstractDAO implements UrnNbnResolvationStatisticDAO {

	private static final Logger LOGGER = Logger.getLogger(UrnNbnResolvationStatisticDaoPostgres.class.getName());

	public UrnNbnResolvationStatisticDaoPostgres(DatabaseConnector connector) {
		super(connector);
	}

	@Override
	public void insertStatistic(Statistic statistic) throws DatabaseException, AlreadyPresentException {
		// checking disabled (optimization)
		// checkRecordNotExists(log);
		DaoOperation operation = new NoResultOperation(buildInsertStatement(statistic));
		try {
			runInTransaction(operation);
		} catch (PersistenceException ex) {
			// should never happen
			LOGGER.log(Level.SEVERE, "Exception unexpected here", ex);
		} catch (SQLException ex) {
			if ("23505".equals(ex.getSQLState())) {
				IdPart registrarCode = new IdPart(ATTR_REGISTRAR_CODE, statistic.getRegistrarCode());
				IdPart year = new IdPart(ATTR_YEAR, Integer.toString(statistic.getYear()));
				IdPart month = new IdPart(ATTR_MONTH, Integer.toString(statistic.getMonth()));
				throw new AlreadyPresentException(new IdPart[] { registrarCode, year, month });
			} else {
				throw new DatabaseException(ex);
			}
		}

	}

	@Override
	public Statistic getStatistic(String registrarCode, int year, int month) throws DatabaseException, RecordNotFoundException {
		StatementWrapper wrapper = new SelectAllAttrsByStringIntInt(TABLE_NAME, ATTR_REGISTRAR_CODE, registrarCode, ATTR_YEAR, year, ATTR_MONTH,
				month);
		DaoOperation operation = new SingleResultOperation(wrapper, new UrnNbnResolvationStatisticRT());
		try {
			return (Statistic) runInTransaction(operation);
		} catch (PersistenceException ex) {
			throw new DatabaseException(ex);
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
	public List<Statistic> listStatistics(String registrarCode) throws DatabaseException {
		StatementWrapper wrapper = new SelectAllAttrsByStringAttr(TABLE_NAME, ATTR_REGISTRAR_CODE, registrarCode);
		DaoOperation operation = new MultipleResultsOperation(wrapper, new UrnNbnResolvationStatisticRT());
		try {
			return (List<Statistic>) runInTransaction(operation);
		} catch (PersistenceException ex) {
			// should never happen
			LOGGER.log(Level.SEVERE, "Exception unexpected here", ex);
			throw new DatabaseException(ex);
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
	public List<Statistic> listStatistics() throws DatabaseException {
		StatementWrapper wrapper = new SelectAllAttrs(TABLE_NAME);
		DaoOperation operation = new MultipleResultsOperation(wrapper, new UrnNbnResolvationStatisticRT());
		try {
			return (List<Statistic>) runInTransaction(operation);
		} catch (PersistenceException ex) {
			// should never happen
			LOGGER.log(Level.SEVERE, "Exception unexpected here", ex);
			throw new DatabaseException(ex);
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	@Override
	public void updateStatistic(Statistic statistic) throws DatabaseException, RecordNotFoundException {
		// checking disabled (optimization)
		// checkRecordExists(log);
		try {
			StatementWrapper wrapper = buildUpdateStatement(statistic);
			DaoOperation operation = new NoResultOperation(wrapper);
			runInTransaction(operation);
		} catch (PersistenceException ex) {
			// should never happen
			// logger.log(Level.SEVERE, "Exception unexpected here", ex);
			throw new DatabaseException(ex);
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}

	private void checkRecordExists(Statistic statistic) throws RecordNotFoundException, DatabaseException {
		getStatistic(statistic.getRegistrarCode(), statistic.getYear(), statistic.getMonth());
	}

	private void checkRecordNotExists(Statistic log) throws DatabaseException, AlreadyPresentException {
		try {
			getStatistic(log.getRegistrarCode(), log.getYear(), log.getMonth());
			IdPart registrarCode = new IdPart(ATTR_REGISTRAR_CODE, log.getRegistrarCode());
			IdPart year = new IdPart(ATTR_YEAR, Integer.toString(log.getYear()));
			IdPart month = new IdPart(ATTR_MONTH, Integer.toString(log.getMonth()));
			throw new AlreadyPresentException(new IdPart[] { registrarCode, year, month });
		} catch (RecordNotFoundException e) {
			// ok, not found
		}
	}

	private StatementWrapper buildInsertStatement(final Statistic statistic) {
		return new StatementWrapper() {

			@Override
			public String preparedStatement() {
				return "INSERT into " + TABLE_NAME//
						+ "(" + ATTR_REGISTRAR_CODE//
						+ "," + ATTR_YEAR//
						+ "," + ATTR_MONTH//
						+ "," + ATTR_RESOLVATIONS//
						+ ") values(?,?,?,?,?,?,?)";
			}

			@Override
			public void populate(PreparedStatement st) throws SyntaxException {
				try {
					st.setString(1, statistic.getRegistrarCode());
					st.setInt(2, statistic.getYear());
					st.setInt(3, statistic.getMonth());
					st.setInt(4, statistic.getVolume());
				} catch (SQLException e) {
					// chyba je v prepared statementu nebo v tranfsformaci resultSetu
					throw new SyntaxException(e);
				}

			}
		};

	}

	private StatementWrapper buildUpdateStatement(final Statistic log) {
		return new StatementWrapper() {

			@Override
			public String preparedStatement() {
				return "UPDATE " + TABLE_NAME + " SET " + ATTR_RESOLVATIONS + "=?" + " WHERE "//
						+ ATTR_REGISTRAR_CODE + "=? AND "//
						+ ATTR_YEAR + "=? AND "//
						+ ATTR_MONTH + "=?";
			}

			@Override
			public void populate(PreparedStatement st) throws SyntaxException {
				try {
					st.setInt(1, log.getVolume());
					st.setString(2, log.getRegistrarCode());
					st.setInt(3, log.getYear());
					st.setInt(4, log.getMonth());
				} catch (SQLException e) {
					// chyba je v prepared statementu nebo v tranfsformaci resultSetu
					throw new SyntaxException(e);
				}
			}
		};
	}
}

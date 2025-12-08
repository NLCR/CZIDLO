package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.UrnNbnResolvationLogsDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.operations.DaoOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.NoResultOperation;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UrnNbnResolvationLogsDaoPostgres extends AbstractDAO implements UrnNbnResolvationLogsDAO {

    private static final Logger LOGGER = Logger.getLogger(UrnNbnResolvationLogsDaoPostgres.class.getName());

    public UrnNbnResolvationLogsDaoPostgres(DatabaseConnector connector) {
        super(connector);
    }

    @Override
    public void insertResolvationAccessLog(String registrarCode, String documentCode) throws DatabaseException {
        DaoOperation operation = new NoResultOperation(buildInsertStatement(registrarCode, documentCode, DateTimeUtils.nowTs()));
        try {
            runInTransaction(operation);
        } catch (PersistenceException ex) {
            // should never happen
            LOGGER.log(Level.SEVERE, "Exception unexpected here", ex);
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    private StatementWrapper buildInsertStatement(String registrarCode, String documentCode, Timestamp now) {
        return new StatementWrapper() {

            @Override
            public String preparedStatement() {
                return "INSERT into " + TABLE_RESOLVATIONS_NAME//
                        + "(" + ATTR_REGISTRAR_CODE//
                        + "," + ATTR_DOCUMENT_CODE//
                        + "," + ATTR_RESOLVED//
                        + ") values(?,?,?)";
            }

            @Override
            public void populate(PreparedStatement st) throws SyntaxException {
                try {
                    st.setString(1, registrarCode);
                    st.setString(2, documentCode);
                    st.setTimestamp(3, now);
                } catch (SQLException e) {
                    // chyba je v prepared statementu nebo v tranfsformaci resultSetu
                    throw new SyntaxException(e);
                }

            }
        };

    }
}

package cz.nkp.urnnbn.core.persistence;

import java.util.List;

import cz.nkp.urnnbn.core.dto.Statistic;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;

public interface UrnNbnStatisticDAO {

    // TODO: rename
    public String TABLE_RESOLVATIONS_NAME = "urnnbn_resolvation_statistics";
    public String TABLE_ASSIGNMENTS_NAME = "urnnbn_assignment_statistics_preprocessed";
    public String ATTR_REGISTRAR_CODE = "registrarCode";
    public String ATTR_YEAR = "year";
    public String ATTR_MONTH = "month";
    public String ATTR_SUM = "sum";
    public String ATTR_ACTIVE = "active";

    public void insertResolvationStatistic(Statistic statistic) throws DatabaseException, AlreadyPresentException;

    public Statistic getResolvationsStatistic(String registrarCode, int year, int month) throws DatabaseException, RecordNotFoundException;

    public Statistic getAssignmentStatistic(String registrarCode, int year, int month) throws DatabaseException, RecordNotFoundException;

    public Statistic getAssignmentStatistic(String registrarCode, int year, int month, boolean active) throws DatabaseException,
            RecordNotFoundException;

    public List<Statistic> listResolvationStatistics(String registrarCode) throws DatabaseException;

    public List<Statistic> listResolvationStatistics() throws DatabaseException;

    public List<Statistic> listAssignmentStatistics(String registrarCode) throws DatabaseException;

    public List<Statistic> listAssignmentStatistics(String registrarCode, boolean active) throws DatabaseException;

    public List<Statistic> listAssignmentStatistics() throws DatabaseException;

    public List<Statistic> listAssignmentStatistics(boolean active) throws DatabaseException;

    public void updateResolvationStatistic(Statistic statistic) throws DatabaseException, RecordNotFoundException;

}

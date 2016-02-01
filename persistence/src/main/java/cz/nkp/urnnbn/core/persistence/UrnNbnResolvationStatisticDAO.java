package cz.nkp.urnnbn.core.persistence;

import java.util.List;

import cz.nkp.urnnbn.core.dto.Statistic;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;

public interface UrnNbnResolvationStatisticDAO {

    public String TABLE_NAME = "UrnNbnResolvationStatistic";
    public String ATTR_REGISTRAR_CODE = "registrarCode";
    public String ATTR_YEAR = "year";
    public String ATTR_MONTH = "month";
    public String ATTR_RESOLVATIONS = "resolvations";

    public void insertStatistic(Statistic statistic) throws DatabaseException, AlreadyPresentException;

    public Statistic getStatistic(String registrarCode, int year, int month) throws DatabaseException, RecordNotFoundException;

    public List<Statistic> listStatistics(String registrarCode) throws DatabaseException;

    public List<Statistic> listStatistics() throws DatabaseException;

    public void updateStatistic(Statistic statistic) throws DatabaseException, RecordNotFoundException;

}

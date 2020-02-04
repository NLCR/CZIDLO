package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.Statistic;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.StatisticService;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class StatisticServiceImpl extends BusinessServiceImpl implements StatisticService {

    private static final Logger LOGGER = Logger.getLogger(StatisticServiceImpl.class.getName());

    private Integer statisticsFirstYearCached = null;

    public StatisticServiceImpl(DatabaseConnector conn) {
        super(conn);
    }

    @Override
    public void incrementResolvationStatistics(String registrarCode) {
        try {
            Calendar now = Calendar.getInstance();
            int year = now.get(Calendar.YEAR);
            int month = now.get(Calendar.MONTH);
            try {
                Statistic statistics = factory.urnNbnStatisticDao().getResolvationsStatistic(registrarCode, year, month);
                statistics.setVolume(statistics.getVolume() + 1);
                LOGGER.fine(statistics.toString());
                factory.urnNbnStatisticDao().updateResolvationStatistic(statistics);
            } catch (RecordNotFoundException e) {
                Statistic initial = new Statistic();
                initial.setRegistrarCode(registrarCode);
                initial.setYear(year);
                initial.setMonth(month);
                initial.setVolume(1);
                try {
                    factory.urnNbnStatisticDao().insertResolvationStatistic(initial);
                } catch (AlreadyPresentException e1) {
                    // should never happen
                }
            }
        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getFirstYearWithData() {
        try {
            if (statisticsFirstYearCached == null) {
                statisticsFirstYearCached = factory.urnDao().getAssignmentsFirstYear();
                if (statisticsFirstYearCached == null) { // still null, i.e. error or no data in database
                    return getCurrentYear();
                }
            }
            return statisticsFirstYearCached;
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    @Override
    public Map<String, Map<Integer, Map<Integer, Integer>>> getUrnNbnAssignmentStatistics(boolean includeActive, boolean includeDeactivated) {
        try {
            boolean filterByActivity = (includeActive && !includeDeactivated) || (!includeActive && includeDeactivated);
            List<Registrar> registrars = factory.registrarDao().getAllRegistrars();
            List<Statistic> data = !filterByActivity ?
                    factory.urnNbnStatisticDao().listAssignmentStatisticsAll() :
                    includeActive ?
                            factory.urnNbnStatisticDao().listAssignmentStatisticsActiveOnly() :
                            factory.urnNbnStatisticDao().listAssignmentStatisticsDeactivatedOnly();
            int yearFrom = getFirstYearWithData();
            int yearTo = getCurrentYear();
            Map<String, Map<Integer, Map<Integer, Integer>>> result = new HashMap<>();
            for (Registrar registrar : registrars) {
                Map<Integer, Map<Integer, Integer>> registrarData = filterAndFill(data, registrar.getCode().toString(), yearFrom, yearTo);
                result.put(registrar.getCode().toString(), registrarData);
            }
            return result;
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Map<Integer, Map<Integer, Integer>> getUrnNbnAssignmentStatistics(String registrarCode, boolean includeActive, boolean includeDeactivated) {
        try {
            boolean filterByActivity = (includeActive && !includeDeactivated) || (!includeActive && includeDeactivated);
            List<Statistic> data = !filterByActivity ?
                    factory.urnNbnStatisticDao().listAssignmentStatisticsAll(registrarCode) :
                    includeActive ?
                            factory.urnNbnStatisticDao().listAssignmentStatisticsActiveOnly(registrarCode) :
                            factory.urnNbnStatisticDao().listAssignmentStatisticsDeactivatedOnly(registrarCode);
            int yearFrom = getFirstYearWithData();
            int yearTo = getCurrentYear();
            return filterAndFill(data, registrarCode, yearFrom, yearTo);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Map<String, Map<Integer, Map<Integer, Integer>>> getUrnNbnResolvationStatistics() {
        try {
            List<Registrar> registrars = factory.registrarDao().getAllRegistrars();
            List<Statistic> data = factory.urnNbnStatisticDao().listResolvationStatistics();
            int yearFrom = getFirstYearWithData();
            int yearTo = getCurrentYear();
            Map<String, Map<Integer, Map<Integer, Integer>>> result = new HashMap<>();
            for (Registrar registrar : registrars) {
                Map<Integer, Map<Integer, Integer>> registrarData = filterAndFill(data, registrar.getCode().toString(), yearFrom, yearTo);
                result.put(registrar.getCode().toString(), registrarData);
            }
            return result;
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Map<Integer, Map<Integer, Integer>> getUrnNbnResolvationStatistics(String registrarCode) {
        try {
            List<Statistic> data = factory.urnNbnStatisticDao().listResolvationStatistics(registrarCode);
            int yearFrom = getFirstYearWithData();
            int yearTo = getCurrentYear();
            return filterAndFill(data, registrarCode, yearFrom, yearTo);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Map<Integer, Map<Integer, Integer>> filterAndFill(List<Statistic> data, String registrarCode, int yearFrom, int yearTo) {
        Map<Integer, Map<Integer, Integer>> result = new HashMap<>();
        for (Statistic record : data) {
            if (registrarCode.equals(record.getRegistrarCode())) {
                Map<Integer, Integer> monthValueMap = result.get(record.getYear());
                if (monthValueMap == null) {
                    monthValueMap = new HashMap<>();
                    result.put(record.getYear(), monthValueMap);
                }
                monthValueMap.put(record.getMonth(), record.getVolume());
            }
        }
        addZeroValues(result, yearFrom, yearTo);
        return result;
    }

    private void addZeroValues(Map<Integer, Map<Integer, Integer>> data, int yearFrom, int yearTo) {
        for (int year = yearFrom; year <= yearTo; year++) {
            Map<Integer, Integer> monthValueMap = data.get(year);
            if (monthValueMap == null) {
                monthValueMap = new HashMap<>();
                data.put(year, monthValueMap);
            }
            for (int month = 1; month <= 12; month++) {
                Integer value = monthValueMap.get(month);
                if (value == null) {
                    monthValueMap.put(month, Integer.valueOf(0));
                }
            }
        }
    }

}

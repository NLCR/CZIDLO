package cz.nkp.urnnbn.server.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.nkp.urnnbn.client.services.GwtStatisticsService;
import cz.nkp.urnnbn.shared.charts.Registrar;
import cz.nkp.urnnbn.shared.charts.Statistic;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GwtStatisticsServiceImpl extends AbstractService implements GwtStatisticsService {

    @Override
    public List<Integer> getAvailableYearsSorted() throws ServerException {
        List<Integer> result = new ArrayList<Integer>();
        for (int year = statisticService.getFirstYearWithData(); year <= statisticService.getCurrentYear(); year++) {
            result.add(Integer.valueOf(year));
        }
        return result;
    }

    @Override
    public Map<String, Map<Integer, Map<Integer, Integer>>> getStatistics(Statistic.Type type, HashMap<Statistic.Option, Serializable> options)
            throws ServerException {
        try {
            switch (type) {
            case URN_NBN_ASSIGNMENTS: {
                boolean includeActive = (boolean) options.get(Statistic.Option.URN_NBN_ASSIGNMENTS_INCLUDE_ACTIVE);
                boolean includeDeactivated = (boolean) options.get(Statistic.Option.URN_NBN_ASSIGNMENTS_INCLUDE_DEACTIVATED);
                return statisticService.getUrnNbnAssignmentStatistics(includeActive, includeDeactivated);
            }
            case URN_NBN_RESOLVATIONS: {
                Map<String, Map<Integer, Map<Integer, Integer>>> result = new HashMap<>();
                for (Registrar registrar : getRegistrars()) {
                    Map<Integer, Map<Integer, Integer>> registrarData = statisticService.getUrnNbnResolvationStatistics(registrar.getCode());
                    result.put(registrar.getCode(), registrarData);
                }
                return result;
            }
            default:
                return null;
            }
        } catch (Throwable e) {
            throw new ServerException(e.getMessage());
        }

    }

    private List<Registrar> getRegistrars() {
        List<cz.nkp.urnnbn.core.dto.Registrar> registrars = readService.registrars();
        List<Registrar> result = new ArrayList<>(registrars.size());
        for (cz.nkp.urnnbn.core.dto.Registrar reg : registrars) {
            Registrar registrar = new Registrar();
            registrar.setCode(reg.getCode().toString());
            registrar.setName(reg.getName());
            result.add(registrar);
        }
        return result;
    }

    @Override
    public Map<Integer, Map<Integer, Integer>> getStatistics(String registrarCode, Statistic.Type type,
            HashMap<Statistic.Option, Serializable> options) throws ServerException {
        try {
            switch (type) {
            case URN_NBN_ASSIGNMENTS:
                boolean includeActive = (boolean) options.get(Statistic.Option.URN_NBN_ASSIGNMENTS_INCLUDE_ACTIVE);
                boolean includeDeactivated = (boolean) options.get(Statistic.Option.URN_NBN_ASSIGNMENTS_INCLUDE_DEACTIVATED);
                return statisticService.getUrnNbnAssignmentStatistics(registrarCode, includeActive, includeDeactivated);
            case URN_NBN_RESOLVATIONS:
                return statisticService.getUrnNbnResolvationStatistics(registrarCode);
            default:
                return null;
            }
        } catch (Throwable e) {
            throw new ServerException(e.getMessage());
        }
    }

}
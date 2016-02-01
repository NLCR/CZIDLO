package cz.nkp.urnnbn.client.services;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.nkp.urnnbn.shared.charts.Statistic;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("statistics")
public interface GwtStatisticsService extends RemoteService {

    List<Integer> getAvailableYearsSorted() throws ServerException;

    /**
     * 
     * @param type
     * @param options
     * @return registrar_code -> year -> month -> value
     */
    Map<String, Map<Integer, Map<Integer, Integer>>> getStatistics(Statistic.Type type, HashMap<Statistic.Option, Serializable> options)
            throws ServerException;

    /**
     * 
     * @param registrarCode
     * @param type
     * @param options
     * @return year -> month -> value
     */
    Map<Integer, Map<Integer, Integer>> getStatistics(String registrarCode, Statistic.Type type, HashMap<Statistic.Option, Serializable> options)
            throws ServerException;

}

package cz.nkp.urnnbn.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.nkp.urnnbn.shared.Registrar;
import cz.nkp.urnnbn.shared.Statistic;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("statistics")
public interface StatisticsService extends RemoteService {

	List<Integer> getAvailableYearsSorted();

	Set<Registrar> getRegistrars();

	/**
	 * 
	 * @param type
	 * @param options
	 * @return registrar_code -> year -> month -> value
	 */
	Map<String, Map<Integer, Map<Integer, Integer>>> getStatistics(Statistic.Type type, HashMap<Statistic.Option, Serializable> options);

	/**
	 * 
	 * @param registrarCode
	 * @param type
	 * @param options
	 * @returnyear -> month -> value
	 */
	Map<Integer, Map<Integer, Integer>> getStatistics(String registrarCode, Statistic.Type type, HashMap<Statistic.Option, Serializable> options);

}

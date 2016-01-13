package cz.nkp.urnnbn.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.nkp.urnnbn.shared.Registrar;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("statistics")
public interface StatisticsService extends RemoteService {

	List<Integer> getAvailableYearsSorted();

	Set<Registrar> getRegistrars();

	Map<String, Map<Integer, Map<Integer, Integer>>> getStatistics(boolean includeActive, boolean includeDeactivated);

	Map<Integer, Map<Integer, Integer>> getStatistics(String registrarCode, boolean includeActive, boolean includeDeactivated);

}

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

	String greetServer(String name) throws IllegalArgumentException;

	@Deprecated
	Map<Registrar, Integer> getTotalRegistrationsByRegistrar();

	List<Integer> getYearsSorted();

	Set<Registrar> getRegistrars();

	Map<Integer, Integer> getAssignmentsByYear(String registrarCode, boolean includeActive, boolean includeDeactivated);

	Map<Integer, Integer> getAssignmentsByMonth(String registrarCode, int year, boolean includeActive, boolean includeDeactivated);

	Map<Integer, Integer> getTotalAssignmentsByYear(boolean includeActive, boolean includeDeactivated);

	Map<Integer, Integer> getTotalAssignmentsByMonth(int year, boolean includeActive, boolean includeDeactivated);

	Map<Integer, Map<String, Integer>> getAssignmentsByYear(boolean includeActive, boolean includeDeactivated);

	Map<Integer, Map<String, Integer>> getAssignmentsByMonth(int year, boolean includeActive, boolean includeDeactivated);

}

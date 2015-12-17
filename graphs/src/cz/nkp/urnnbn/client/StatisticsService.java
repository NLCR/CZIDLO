package cz.nkp.urnnbn.client;

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
	
	Set<Registrar> getRegistrars();
	
	Map<Integer, Integer> getAssignmentsByYear(String registrarCode);
	
	Map<Integer, Integer> getAssignmentsByMonth(String registrarCode, int year);
	
	Map<Integer, Map<String, Integer>> getAssignmentsByYear();
	
	Map<Integer, Map<String, Integer>> getAssignmentsByMonth(int year);
	
	
}

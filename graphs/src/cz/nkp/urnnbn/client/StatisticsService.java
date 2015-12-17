package cz.nkp.urnnbn.client;

import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.nkp.urnnbn.shared.Registrar;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("statistics")
public interface StatisticsService extends RemoteService {

	String greetServer(String name) throws IllegalArgumentException;

	Map<Integer, Map<String, Integer>> getRegistrationPerYears();

	Map<Registrar, Integer> getTotalRegistrationsByRegistrar();
}

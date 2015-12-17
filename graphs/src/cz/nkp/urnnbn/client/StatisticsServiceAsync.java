package cz.nkp.urnnbn.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import cz.nkp.urnnbn.shared.Registrar;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface StatisticsServiceAsync {

	void greetServer(String input, AsyncCallback<String> callback) throws IllegalArgumentException;

	void getAssignmentsByYear(AsyncCallback<Map<Integer, Map<String, Integer>>> callback);

	void getTotalRegistrationsByRegistrar(AsyncCallback<Map<Registrar, Integer>> callback);

	void getRegistrars(AsyncCallback<Set<Registrar>> callback);

	void getAssignmentsByYear(String registrarCode, AsyncCallback<Map<Integer, Integer>> callback);

	void getAssignmentsByMonth(int year, AsyncCallback<Map<Integer, Map<String, Integer>>> callback);

	void getAssignmentsByMonth(String registrarCode, int year, AsyncCallback<Map<Integer, Integer>> callback);

	void getYearsSorted(AsyncCallback<List<Integer>> callback);

	void getTotalAssignmentsByYear(AsyncCallback<Map<Integer, Integer>> callback);

	void getTotalAssignmentsByMonth(int year, AsyncCallback<Map<Integer, Integer>> callback);
}

package cz.nkp.urnnbn.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import cz.nkp.urnnbn.shared.Registrar;

public interface StatisticsServiceAsync {

	void greetServer(String input, AsyncCallback<String> callback) throws IllegalArgumentException;

	void getYearsSorted(AsyncCallback<List<Integer>> callback);

	void getRegistrars(AsyncCallback<Set<Registrar>> callback);

	void getAssignmentsByYear(boolean includeActive, boolean includeDeactivated, AsyncCallback<Map<Integer, Map<String, Integer>>> callback);

	void getTotalRegistrationsByRegistrar(AsyncCallback<Map<Registrar, Integer>> callback);

	void getAssignmentsByYear(String registrarCode, boolean includeActive, boolean includeDeactivated, AsyncCallback<Map<Integer, Integer>> callback);

	void getAssignmentsByMonth(int year, boolean includeActive, boolean includeDeactivated, AsyncCallback<Map<Integer, Map<String, Integer>>> callback);

	void getAssignmentsByMonth(String registrarCode, int year, boolean includeActive, boolean includeDeactivated,
			AsyncCallback<Map<Integer, Integer>> callback);

	void getTotalAssignmentsByYear(boolean includeActive, boolean includeDeactivated, AsyncCallback<Map<Integer, Integer>> callback);

	void getTotalAssignmentsByMonth(int year, boolean includeActive, boolean includeDeactivated, AsyncCallback<Map<Integer, Integer>> callback);

	void getRegistrarNames(AsyncCallback<Map<String, String>> callback);
}

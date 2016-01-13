package cz.nkp.urnnbn.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import cz.nkp.urnnbn.shared.Registrar;

public interface StatisticsServiceAsync {

	void getAvailableYearsSorted(AsyncCallback<List<Integer>> callback);

	void getRegistrars(AsyncCallback<Set<Registrar>> callback);

	void getStatistics(String registrarCode, boolean includeActive, boolean includeDeactivated,
			AsyncCallback<Map<Integer, Map<Integer, Integer>>> callback);

	void getStatistics(boolean includeActive, boolean includeDeactivated, AsyncCallback<Map<String, Map<Integer, Map<Integer, Integer>>>> callback);

}

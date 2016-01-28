package cz.nkp.urnnbn.client.services;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import cz.nkp.urnnbn.shared.charts.Registrar;
import cz.nkp.urnnbn.shared.charts.Statistic;

public interface StatisticsServiceAsync {

	void getAvailableYearsSorted(AsyncCallback<List<Integer>> callback);

	void getRegistrars(AsyncCallback<Set<Registrar>> callback);

	void getStatistics(String registrarCode, Statistic.Type type, HashMap<Statistic.Option, Serializable> options,
			AsyncCallback<Map<Integer, Map<Integer, Integer>>> callback);

	void getStatistics(Statistic.Type type, HashMap<Statistic.Option, Serializable> options,
			AsyncCallback<Map<String, Map<Integer, Map<Integer, Integer>>>> callback);

}

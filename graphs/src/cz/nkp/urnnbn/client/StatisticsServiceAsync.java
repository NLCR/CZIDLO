package cz.nkp.urnnbn.client;

import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import cz.nkp.urnnbn.shared.Registrar;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface StatisticsServiceAsync {

	void greetServer(String input, AsyncCallback<String> callback) throws IllegalArgumentException;

	void getRegistrationPerYears(AsyncCallback<Map<Integer, Map<String, Integer>>> callback);

	void getTotalRegistrationsByRegistrar(AsyncCallback<Map<Registrar, Integer>> callback);
}

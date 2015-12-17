package cz.nkp.urnnbn.server;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import cz.nkp.urnnbn.client.StatisticsService;
import cz.nkp.urnnbn.shared.FieldVerifier;
import cz.nkp.urnnbn.shared.Registrar;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class StatisticsServiceImpl extends RemoteServiceServlet implements
		StatisticsService {

	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid. 
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
	
	@Override
	public Map<Integer, Map<String, Integer>> getRegistrationPerYears() {
		RegistrarsManager registrars = RegistrarsManager.getInstance();
		Map<Integer, Map<String, Integer>> result = new HashMap<>();
		for (RegistrarRegistrationsData registrar : registrars.getRegistrars()) {
			for (Integer year : registrar.getActiveYears()) {
				Map<String, Integer> yearMap = result.get(year);
				if (yearMap == null) {
					yearMap = new HashMap<>();
					result.put(year, yearMap);
				}
				yearMap.put(registrar.getRegistrar().getCode(),
						registrar.getRegistrations(year));
			}
		}
		return result;
	}

	@Override
	public Map<Registrar, Integer> getTotalRegistrationsByRegistrar() {
		RegistrarsManager regManager = RegistrarsManager.getInstance();
		Map<Registrar, Integer> result = new HashMap<>();
		for (RegistrarRegistrationsData registrationData : regManager.getRegistrars()) {
			int total = registrationData.getRegistrationsTotal();
			result.put(registrationData.getRegistrar(), total);
		}
		return result;
	}
}

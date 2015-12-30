package cz.nkp.urnnbn.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import cz.nkp.urnnbn.client.StatisticsService;
import cz.nkp.urnnbn.shared.FieldVerifier;
import cz.nkp.urnnbn.shared.Registrar;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class StatisticsServiceImpl extends RemoteServiceServlet implements StatisticsService {

	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid.
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException("Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		return "Hello, " + input + "!<br><br>I am running " + serverInfo + ".<br><br>It looks like you are using:<br>" + userAgent;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to prevent cross-site script vulnerabilities.
	 * 
	 * @param html
	 *            the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	@Override
	public Map<Registrar, Integer> getTotalRegistrationsByRegistrar() {
		RegistrarsManager registrarMgr = RegistrarsManager.getInstance();
		Map<Registrar, Integer> result = new HashMap<>();
		for (Registrar registrar : registrarMgr.getRegistrars()) {
			Assignments assignments = registrarMgr.getAssignmentData(registrar.getCode());
			int total = assignments.getRegistrationsTotal();
			result.put(registrar, total);
		}
		return result;
	}

	@Override
	public List<Integer> getYearsSorted() {
		return RegistrarsManager.getInstance().getYears();
	}

	@Override
	public Set<Registrar> getRegistrars() {
		return RegistrarsManager.getInstance().getRegistrars();
	}

	@Override
	public Map<Integer, Integer> getAssignmentsByYear(String registrarCode, boolean includeActive, boolean includeDeactivated) {
		double factor = getDecreaseFactor(includeActive, includeDeactivated);
		Map<Integer, Integer> original = RegistrarsManager.getInstance().getAssignmentData(registrarCode).getAnnualAssignments();
		Map<Integer, Integer> result = new HashMap<>();
		for (Integer key : original.keySet()) {
			result.put(key, (int) (original.get(key) * factor));
		}
		return result;
	}

	@Override
	public Map<Integer, Integer> getAssignmentsByMonth(String registrarCode, int year, boolean includeActive, boolean includeDeactivated) {
		double factor = getDecreaseFactor(includeActive, includeDeactivated);
		RegistrarsManager registraMgr = RegistrarsManager.getInstance();
		Map<Integer, Integer> result = new HashMap<>();
		for (Integer month : registraMgr.getMonths()) {
			result.put(month, 0);
		}
		Assignments assignments = registraMgr.getAssignmentData(registrarCode);
		for (Integer month : assignments.getActiveMonths(year)) {
			// System.out.println("year: " + year + ", month: " + month);
			int inMonth = (int) (assignments.getRegistrations(year, month) * factor);
			result.put(month, inMonth);
		}
		return result;
	}

	@Override
	public Map<Integer, Map<String, Integer>> getAssignmentsByYear(boolean includeActive, boolean includeDeactivated) {
		double factor = getDecreaseFactor(includeActive, includeDeactivated);
		RegistrarsManager registraMgr = RegistrarsManager.getInstance();
		Map<Integer, Map<String, Integer>> result = new HashMap<>();
		for (Registrar registrar : registraMgr.getRegistrars()) {
			Assignments assignments = registraMgr.getAssignmentData(registrar.getCode());
			for (Integer year : assignments.getActiveYears()) {
				Map<String, Integer> yearMap = result.get(year);
				if (yearMap == null) {
					yearMap = new HashMap<>();
					result.put(year, yearMap);
				}
				yearMap.put(registrar.getCode(), (int) (assignments.getRegistrations(year) * factor));
			}
		}
		return result;
	}

	@Override
	public Map<Integer, Map<String, Integer>> getAssignmentsByMonth(int year, boolean includeActive, boolean includeDeactivated) {
		double factor = getDecreaseFactor(includeActive, includeDeactivated);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Integer> getTotalAssignmentsByYear(boolean includeActive, boolean includeDeactivated) {
		double factor = getDecreaseFactor(includeActive, includeDeactivated);
		RegistrarsManager registraMgr = RegistrarsManager.getInstance();
		List<Integer> years = registraMgr.getYears();
		Map<Integer, Integer> result = new HashMap<>();
		for (Integer year : years) {
			result.put(year, 0);
		}
		for (Registrar registrar : registraMgr.getRegistrars()) {
			Assignments assignments = registraMgr.getAssignmentData(registrar.getCode());
			for (Integer year : assignments.getActiveYears()) {
				Integer soFar = result.get(year);// teoreticky muze byt null
				soFar += (int) (assignments.getRegistrations(year) * factor);
				result.put(year, soFar);
			}
		}
		return result;
	}

	@Override
	public Map<Integer, Integer> getTotalAssignmentsByMonth(int year, boolean includeActive, boolean includeDeactivated) {
		double factor = getDecreaseFactor(includeActive, includeDeactivated);
		RegistrarsManager registraMgr = RegistrarsManager.getInstance();
		Map<Integer, Integer> result = new HashMap<>();
		List<Integer> months = registraMgr.getMonths();
		for (Integer month : months) {
			result.put(month, 0);
		}
		for (Registrar registrar : registraMgr.getRegistrars()) {
			Assignments assignments = registraMgr.getAssignmentData(registrar.getCode());
			for (Integer month : assignments.getActiveMonths(year)) {
				Integer soFar = result.get(month);// teoreticky muze byt null
				// System.out.println("year: " + year + ", month: " + month);
				soFar += (int) (assignments.getRegistrations(year, month) * factor);
				result.put(month, soFar);
			}
		}
		return result;
	}

	private double getDecreaseFactor(boolean includeActive, boolean includeDeactivated) {
		return includeActive && includeDeactivated ? 1.0 : includeActive ? 0.9 : includeDeactivated ? 0.1 : 0.0;
	}

}

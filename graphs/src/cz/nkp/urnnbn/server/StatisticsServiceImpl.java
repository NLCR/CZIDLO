package cz.nkp.urnnbn.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import cz.nkp.urnnbn.client.charts.StatisticsService;
import cz.nkp.urnnbn.shared.charts.Registrar;
import cz.nkp.urnnbn.shared.charts.Statistic;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class StatisticsServiceImpl extends RemoteServiceServlet implements StatisticsService {

	@Override
	public List<Integer> getAvailableYearsSorted() {
		return RegistrarsManager.getInstance().getYears();
	}

	@Override
	public Set<Registrar> getRegistrars() {
		return RegistrarsManager.getInstance().getRegistrars();
	}

	@Override
	public Map<String, Map<Integer, Map<Integer, Integer>>> getStatistics(Statistic.Type type, HashMap<Statistic.Option, Serializable> options) {
		switch (type) {
		case URN_NBN_ASSIGNEMNTS:
			boolean includeActive = (boolean) options.get(Statistic.Option.URN_NBN_ASSIGNEMNTS_INCLUDE_ACTIVE);
			boolean includeDeactivated = (boolean) options.get(Statistic.Option.URN_NBN_ASSIGNEMNTS_INCLUDE_DEACTIVATED);
			return getUrnNbnAssignments(includeActive, includeDeactivated);
		case URN_NBN_RESOLVATIONS:
			// TODO
		default:
			return null;
		}
	}

	private Map<String, Map<Integer, Map<Integer, Integer>>> getUrnNbnAssignments(boolean includeActive, boolean includeDeactivated) {
		Map<String, Map<Integer, Map<Integer, Integer>>> result = new HashMap<>();
		for (Registrar registrar : getRegistrars()) {
			result.put(registrar.getCode(), getUrnNbnAssignments(registrar.getCode(), includeActive, includeDeactivated));
		}
		return result;
	}

	@Override
	public Map<Integer, Map<Integer, Integer>> getStatistics(String registrarCode, Statistic.Type type,
			HashMap<Statistic.Option, Serializable> options) {
		switch (type) {
		case URN_NBN_ASSIGNEMNTS:
			boolean includeActive = (boolean) options.get(Statistic.Option.URN_NBN_ASSIGNEMNTS_INCLUDE_ACTIVE);
			boolean includeDeactivated = (boolean) options.get(Statistic.Option.URN_NBN_ASSIGNEMNTS_INCLUDE_DEACTIVATED);
			return getUrnNbnAssignments(registrarCode, includeActive, includeDeactivated);
		case URN_NBN_RESOLVATIONS:
			// TODO
		default:
			return null;
		}
	}

	private Map<Integer, Map<Integer, Integer>> getUrnNbnAssignments(String registrarCode, boolean includeActive, boolean includeDeactivated) {
		double factor = getDecreaseFactor(includeActive, includeDeactivated);
		Map<Integer, Map<Integer, Integer>> result = new HashMap<>();
		Assignments assignments = RegistrarsManager.getInstance().getAssignmentData(registrarCode);
		List<Integer> years = getAvailableYearsSorted();
		List<Integer> months = getMothsSorted();
		for (Integer year : years) {
			Map<Integer, Integer> annualData = new HashMap<>();
			for (Integer month : months) {
				Integer registrations = assignments.getRegistrations(year, month);
				annualData.put(month, (int) (registrations * factor));
			}
			result.put(year, annualData);
		}
		return result;
	}

	private double getDecreaseFactor(boolean includeActive, boolean includeDeactivated) {
		return includeActive && includeDeactivated ? 1.0 : includeActive ? 0.9 : includeDeactivated ? 0.1 : 0.0;
	}

	private List<Integer> getMothsSorted() {
		List<Integer> result = new ArrayList<>();
		for (int month = 1; month <= 12; month++) {
			result.add(month);
		}
		return result;
	}

	// @Override
	// public Map<Registrar, Integer> getTotalRegistrationsByRegistrar() {
	// RegistrarsManager registrarMgr = RegistrarsManager.getInstance();
	// Map<Registrar, Integer> result = new HashMap<>();
	// for (Registrar registrar : registrarMgr.getRegistrars()) {
	// Assignments assignments = registrarMgr.getAssignmentData(registrar.getCode());
	// int total = assignments.getRegistrationsTotal();
	// result.put(registrar, total);
	// }
	// return result;
	// }

	// @Override
	// public Map<Integer, Integer> getAssignmentsByYear(String registrarCode, boolean includeActive, boolean includeDeactivated) {
	// double factor = getDecreaseFactor(includeActive, includeDeactivated);
	// Map<Integer, Integer> original = RegistrarsManager.getInstance().getAssignmentData(registrarCode).getAnnualAssignments();
	// Map<Integer, Integer> result = new HashMap<>();
	// for (Integer key : original.keySet()) {
	// result.put(key, (int) (original.get(key) * factor));
	// }
	// return result;
	// }
	//
	// @Override
	// public Map<Integer, Integer> getAssignmentsByMonth(String registrarCode, int year, boolean includeActive, boolean includeDeactivated) {
	// double factor = getDecreaseFactor(includeActive, includeDeactivated);
	// RegistrarsManager registraMgr = RegistrarsManager.getInstance();
	// Map<Integer, Integer> result = new HashMap<>();
	// for (Integer month : registraMgr.getMonths()) {
	// result.put(month, 0);
	// }
	// Assignments assignments = registraMgr.getAssignmentData(registrarCode);
	// for (Integer month : assignments.getActiveMonths(year)) {
	// // System.out.println("year: " + year + ", month: " + month);
	// int inMonth = (int) (assignments.getRegistrations(year, month) * factor);
	// result.put(month, inMonth);
	// }
	// return result;
	// }

	// @Override
	// public Map<Integer, Map<String, Integer>> getAssignmentsByYear(boolean includeActive, boolean includeDeactivated) {
	// double factor = getDecreaseFactor(includeActive, includeDeactivated);
	// RegistrarsManager registraMgr = RegistrarsManager.getInstance();
	// Map<Integer, Map<String, Integer>> result = new HashMap<>();
	// for (Registrar registrar : registraMgr.getRegistrars()) {
	// Assignments assignments = registraMgr.getAssignmentData(registrar.getCode());
	// for (Integer year : assignments.getActiveYears()) {
	// Map<String, Integer> yearMap = result.get(year);
	// if (yearMap == null) {
	// yearMap = new HashMap<>();
	// result.put(year, yearMap);
	// }
	// yearMap.put(registrar.getCode(), (int) (assignments.getRegistrations(year) * factor));
	// }
	// }
	// return result;
	// }
	//
	// @Override
	// public Map<Integer, Map<String, Integer>> getAssignmentsByMonth(int year, boolean includeActive, boolean includeDeactivated) {
	// double factor = getDecreaseFactor(includeActive, includeDeactivated);
	// RegistrarsManager registraMgr = RegistrarsManager.getInstance();
	// Map<Integer, Map<String, Integer>> result = new HashMap<>();
	// for (Registrar registrar : registraMgr.getRegistrars()) {
	// Assignments assignments = registraMgr.getAssignmentData(registrar.getCode());
	// for (int month = 1; month <= 12; month++) {
	// Map<String, Integer> map = result.get(month);
	// if (map == null) {
	// map = new HashMap<String, Integer>();
	// result.put(month, map);
	// }
	// int value = (int) (assignments.getRegistrations(year, month) * factor);
	// map.put(registrar.getCode(), value);
	// }
	// }
	// return result;
	// }
	//
	// @Override
	// public Map<Integer, Integer> getTotalAssignmentsByYear(boolean includeActive, boolean includeDeactivated) {
	// double factor = getDecreaseFactor(includeActive, includeDeactivated);
	// RegistrarsManager registraMgr = RegistrarsManager.getInstance();
	// List<Integer> years = registraMgr.getYears();
	// Map<Integer, Integer> result = new HashMap<>();
	// for (Integer year : years) {
	// result.put(year, 0);
	// }
	// for (Registrar registrar : registraMgr.getRegistrars()) {
	// Assignments assignments = registraMgr.getAssignmentData(registrar.getCode());
	// for (Integer year : assignments.getActiveYears()) {
	// Integer soFar = result.get(year);// teoreticky muze byt null
	// soFar += (int) (assignments.getRegistrations(year) * factor);
	// result.put(year, soFar);
	// }
	// }
	// return result;
	// }
	//
	// @Override
	// public Map<Integer, Integer> getTotalAssignmentsByMonth(int year, boolean includeActive, boolean includeDeactivated) {
	// double factor = getDecreaseFactor(includeActive, includeDeactivated);
	// RegistrarsManager registraMgr = RegistrarsManager.getInstance();
	// Map<Integer, Integer> result = new HashMap<>();
	// List<Integer> months = registraMgr.getMonths();
	// for (Integer month : months) {
	// result.put(month, 0);
	// }
	// for (Registrar registrar : registraMgr.getRegistrars()) {
	// Assignments assignments = registraMgr.getAssignmentData(registrar.getCode());
	// for (Integer month : assignments.getActiveMonths(year)) {
	// Integer soFar = result.get(month);// teoreticky muze byt null
	// // System.out.println("year: " + year + ", month: " + month);
	// soFar += (int) (assignments.getRegistrations(year, month) * factor);
	// result.put(month, soFar);
	// }
	// }
	// return result;
	// }

	// @Override
	// public Map<String, String> getRegistrarNames() {
	// RegistrarsManager registraMgr = RegistrarsManager.getInstance();
	// Map<String, String> result = new HashMap<>();
	// for (Registrar registrar : registraMgr.getRegistrars()) {
	// result.put(registrar.getCode(), registrar.getName());
	// }
	// return result;
	// }

}

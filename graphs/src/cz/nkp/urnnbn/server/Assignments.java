package cz.nkp.urnnbn.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Assignments {

	private Map<Integer, Map<Integer, Integer>> yearsMap = new HashMap<>();

	public void setAssignments(int year, int month, int assignments) {
		Map<Integer, Integer> monthMap = getMonthMap(year);
		if (monthMap == null) {
			monthMap = new HashMap<>();
			yearsMap.put(year, monthMap);
		}
		monthMap.put(month, assignments);
	}

	private Map<Integer, Integer> getMonthMap(int year) {
		Map<Integer, Integer> monthMap = yearsMap.get(year);
		if (monthMap == null) {
			monthMap = new HashMap<>();
			yearsMap.put(year, monthMap);
		}
		return monthMap;
	}

	public Set<Integer> getActiveYears() {
		return yearsMap.keySet();
	}

	public int getRegistrations(int year, int month) {
		Map<Integer, Integer> monthMap = yearsMap.get(year);
		if (monthMap == null) {
			return 0;
		} else {
			Integer registrations = monthMap.get(month);
			return registrations != null ? registrations : 0;
		}
	}

	public Map<Integer, Integer> getAnnualAssignments() {
		Map<Integer, Integer> result = new HashMap<>();
		for (Integer year : yearsMap.keySet()) {
			Integer allMonths = getRegistrations(year);
			result.put(year, allMonths);
		}
		return result;
	}

	public int getRegistrations(int year) {
		Map<Integer, Integer> monthMap = yearsMap.get(year);
		if (monthMap == null) {
			return 0;
		} else {
			int sum = 0;
			for (int registrations : monthMap.values()) {
				sum += registrations;
			}
			return sum;
		}
	}

	public int getRegistrationsTotal() {
		int sum = 0;
		for (Map<Integer, Integer> months : yearsMap.values()) {
			for (Integer registrationsInMonth : months.values()) {
				sum += registrationsInMonth;
			}
		}
		return sum;
	}

	public Set<Integer> getActiveMonths(int year) {
		Map<Integer, Integer> months = yearsMap.get(year);
		if (months == null) {
			return Collections.emptySet();
		} else {
			return yearsMap.get(year).keySet();
		}
	}

}

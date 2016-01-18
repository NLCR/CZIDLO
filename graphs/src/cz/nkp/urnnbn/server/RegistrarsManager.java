package cz.nkp.urnnbn.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.nkp.urnnbn.shared.charts.Registrar;

public class RegistrarsManager {

	private static RegistrarsManager instance;
	private Set<Registrar> registrars = new HashSet<>();
	private Map<String, Statistics> assignmentsByRegistrarCode = new HashMap<>();
	private Map<String, Statistics> resolvaltionsByRegistrarCode = new HashMap<>();
	private final int yearStart;
	private final int yearEnd;

	public RegistrarsManager(int yearStart, int yearEnd) {
		this.yearStart = yearStart;
		this.yearEnd = yearEnd;
	}

	public void addRegistrar(Registrar registrar, Statistics assignments, Statistics resolvations) {
		registrars.add(registrar);
		assignmentsByRegistrarCode.put(registrar.getCode(), assignments);
		resolvaltionsByRegistrarCode.put(registrar.getCode(), resolvations);
	}

	public List<Integer> getAvailableYears() {
		List<Integer> result = new ArrayList<>();
		for (int year = yearStart; year <= yearEnd; year++) {
			result.add(year);
		}
		return result;
	}

	public Set<Registrar> getRegistrars() {
		return registrars;
	}

	public Statistics getAssignmentData(String code) {
		return assignmentsByRegistrarCode.get(code);
	}

	public Statistics getResolvationsData(String code) {
		return resolvaltionsByRegistrarCode.get(code);
	}

	public static RegistrarsManager getInstance() {
		// return buildInstance();
		if (instance == null) {
			instance = buildInstance();
		}
		return instance;
	}

	private static RegistrarsManager buildInstance() {
		int yearStart = 2012;
		int yearEnd = 2015;
		RegistrarsManager result = new RegistrarsManager(yearStart, yearEnd);
		// result.addRegistrar(RegistrarBuilder.buildMzk(), AssignmentsFactory.buildLinearAssignment(yearStart, yearEnd, 200, 20));
		// result.addRegistrar(RegistrarBuilder.buildNlk(), AssignmentsFactory.buildLinearAssignment(yearStart, yearEnd, 250, 15));
		// result.addRegistrar(RegistrarBuilder.buildNkp(), AssignmentsFactory.buildLinearAssignment(yearStart, yearEnd, 300, 1));
		// result.addRegistrar(RegistrarBuilder.buildKnav(), AssignmentsFactory.buildLinearAssignment(yearStart, yearEnd, 100, 0));
		// result.addRegistrar(RegistrarBuilder.buildMuni(), AssignmentsFactory.buildStaticPartOfYearAssignment(yearEnd, 5, 10, 150));
		// result.addRegistrar(RegistrarBuilder.buildMzk(), AssignmentsFactory.buildLinearAssignment(yearStart, yearEnd, 200, 5));
		// result.addRegistrar(RegistrarBuilder.buildNlk(), AssignmentsFactory.buildLinearAssignment(yearStart, yearEnd, 250, 4));
		// result.addRegistrar(RegistrarBuilder.buildNkp(), AssignmentsFactory.buildLinearAssignment(yearStart, yearEnd, 300, 1));
		// result.addRegistrar(RegistrarBuilder.buildKnav(), AssignmentsFactory.buildLinearAssignment(yearStart, yearEnd, 100, 0));
		// result.addRegistrar(RegistrarBuilder.buildMuni(), AssignmentsFactory.buildStaticPartOfYearAssignment(yearEnd, 5, 10, 7));

		result.addRegistrar(new Registrar("nkp", "Národní knihovna v Praze"),//
				StatisticsFactory.buildRandomStatistics(yearStart, yearEnd, 100, 120, 1.05f),//
				StatisticsFactory.buildRandomStatistics(yearStart + 1, yearEnd, 500, 1200, 1.05f));
		result.addRegistrar(new Registrar("mzk", "Moravská zemská knihovna"),//
				StatisticsFactory.buildRandomStatistics(yearStart, yearEnd, 50, 60, 1.2f),//
				StatisticsFactory.buildRandomStatistics(yearStart, yearEnd, 50, 600, 1.2f));
		result.addRegistrar(new Registrar("knav", "Knihovna akademie věd"),//
				StatisticsFactory.buildRandomStatistics(yearStart, yearEnd, 20, 22, 1.0f),//
				StatisticsFactory.buildRandomStatistics(yearStart, yearEnd, 40, 220, 1.0f));
		result.addRegistrar(new Registrar("muni", "Masarykova univerzita"), //
				StatisticsFactory.buildRandomStatistics(yearStart + 1, yearEnd, 30, 35, 0.9f),//
				StatisticsFactory.buildRandomStatistics(yearStart + 1, yearEnd, 10, 100, 0.9f));
		result.addRegistrar(new Registrar("nlk", "Národní lékařská knihovna"),//
				StatisticsFactory.buildRandomStatistics(2012, 2014, 10, 50, 1.3f),//
				StatisticsFactory.buildRandomStatistics(2012, 2014, 100, 500, 1.3f));
		result.addRegistrar(new Registrar("ova", "Ostravská univerzita v Ostravě - Univerzitní knihovna"), //
				StatisticsFactory.buildRandomStatistics(2013, 2014, 30, 33, 0.8f),//
				StatisticsFactory.buildRandomStatistics(2013, 2014, 300, 530, 0.8f));

		// Random random = new Random();
		// for (int i = 0; i < 1; i++) {
		// result.addRegistrar(RegistrarBuilder.buildRegistrar(i),
		// // AssignmentsFactory.buildLinearAssignment(yearStart, yearEnd, random.nextInt(5), random.nextInt(0)));
		// // AssignmentsFactory.buildLinearAssignment(yearStart, yearEnd, random.nextInt(100), random.nextInt(10)));
		// AssignmentsFactory.buildLinearAssignment(yearStart, yearEnd, random.nextInt(100), 0));
		// }
		return result;
	}

	public List<Integer> getMonths() {
		List<Integer> result = new ArrayList<>();
		for (int i = 1; i <= 12; i++) {
			result.add(i);
		}
		return result;
	}

}

package cz.nkp.urnnbn.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cz.nkp.urnnbn.shared.Registrar;

public class RegistrarsManager {

	private static RegistrarsManager instance;
	private Map<String, Assignments> assignmentsByRegistrarCode = new HashMap<>();
	private Set<Registrar> registrars = new HashSet<>();
	private final int yearStart;
	private final int yearEnd;

	public RegistrarsManager(int yearStart, int yearEnd) {
		this.yearStart = yearStart;
		this.yearEnd = yearEnd;
	}

	public void addRegistrar(Registrar registrar, Assignments assignments) {
		registrars.add(registrar);
		assignmentsByRegistrarCode.put(registrar.getCode(), assignments);
	}

	public List<Integer> getYears() {
		List<Integer> result = new ArrayList<>();
		for (int year = yearStart; year <= yearEnd; year++) {
			result.add(year);
		}
		return result;
	}

	public Set<Registrar> getRegistrars() {
		return registrars;
	}

	public Assignments getAssignmentData(String code) {
		return assignmentsByRegistrarCode.get(code);
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

		result.addRegistrar(RegistrarBuilder.buildNkp(), AssignmentsFactory.buildRandomAssignment(yearStart, yearEnd, 100, 120, 1.05f));
		result.addRegistrar(RegistrarBuilder.buildMzk(), AssignmentsFactory.buildRandomAssignment(yearStart, yearEnd, 50, 60, 1.2f));
		result.addRegistrar(RegistrarBuilder.buildKnav(), AssignmentsFactory.buildRandomAssignment(yearStart, yearEnd, 20, 22, 1.0f));
		result.addRegistrar(RegistrarBuilder.buildMuni(), AssignmentsFactory.buildRandomAssignment(yearStart + 1, yearEnd, 30, 35, 0.9f));
		result.addRegistrar(RegistrarBuilder.buildNlk(), AssignmentsFactory.buildRandomAssignment(2012, 2014, 10, 50, 1.3f));
		result.addRegistrar(RegistrarBuilder.buildOva(), AssignmentsFactory.buildRandomAssignment(2013, 2014, 30, 33, 0.8f));

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

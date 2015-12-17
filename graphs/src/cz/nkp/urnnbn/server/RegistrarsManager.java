package cz.nkp.urnnbn.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cz.nkp.urnnbn.shared.Registrar;

public class RegistrarsManager {

	private Map<String, Assignments> assignmentsByRegistrarCode = new HashMap<>();
	private Set<Registrar> registrars = new HashSet<>();

	public void addRegistrar(Registrar registrar, Assignments assignments) {
		registrars.add(registrar);
		assignmentsByRegistrarCode.put(registrar.getCode(), assignments);
	}

	public Set<Registrar> getRegistrars() {
		return registrars;
	}

	public Assignments getAssignmentData(String code) {
		return assignmentsByRegistrarCode.get(code);
	}

	public static RegistrarsManager getInstance() {
		// return instance;
		return buildInstance();
	}

	private static RegistrarsManager buildInstance() {
		RegistrarsManager result = new RegistrarsManager();
		int yearStart = 2013;
		int yearEnd = 2015;
		result.addRegistrar(RegistrarBuilder.buildMzk(), AssignmentsFactory.buildLinearAssignment(yearStart, yearEnd, 2, 2));
		result.addRegistrar(RegistrarBuilder.buildNkp(), AssignmentsFactory.buildLinearAssignment(yearStart, yearEnd, 3, 1));
		result.addRegistrar(RegistrarBuilder.buildKnav(), AssignmentsFactory.buildLinearAssignment(yearStart, yearEnd, 1, 0));
		// Random random = new Random();
		// for (int i = 0; i < 5; i++) {
		// result.addRegistrar(RegistrarBuilder.buildRegistrar(i),
		// AssignmentsFactory.buildLinearAssignment(yearStart, yearEnd, random.nextInt(5), random.nextInt(5)));
		// }
		return result;
	}

}

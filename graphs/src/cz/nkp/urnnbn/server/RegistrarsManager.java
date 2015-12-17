package cz.nkp.urnnbn.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cz.nkp.urnnbn.shared.Registrar;

public class RegistrarsManager {

	// public static RegistrarsManager instance = initInstance();

	private Map<String, RegistrarRegistrationsData> registrarCodeMap = new HashMap<>();

	public void addRegistrar(RegistrarRegistrationsData registrar) {
		registrarCodeMap.put(registrar.getRegistrar().getCode(), registrar);
	}

	public Collection<RegistrarRegistrationsData> getRegistrars() {
		return registrarCodeMap.values();
	}

	public RegistrarRegistrationsData getRegistrar(String code) {
		return registrarCodeMap.get(code);
	}

	public static RegistrarsManager getInstance() {
		// return instance;
		return initInstance();
	}

	private static RegistrarsManager initInstance() {
		RegistrarsManager result = new RegistrarsManager();
		result.addRegistrar(nkp());
		result.addRegistrar(mzk());
		result.addRegistrar(knav());
		Random random = new Random();
//		for (int i = 0; i < 1; i++) {
//			result.addRegistrar(randomRegistrar(i, random));
//		}
		return result;
	}

	private static RegistrarRegistrationsData randomRegistrar(int i,
			Random random) {
		RegistrarRegistrationsData result = new RegistrarRegistrationsData(
				new Registrar("generated_" + i, "Knihovna " + i));
		for (int year = 2013; year <= 2015; year++) {
			for (int month = 1; month <= 12; month++) {
				int registrations = random.nextInt(5)+1;
				// current -= 10 * i;
				result.setMonthRegistrations(year, month, registrations);
			}
		}
		return result;
	}

	private static RegistrarRegistrationsData knav() {
		RegistrarRegistrationsData result = new RegistrarRegistrationsData(
				new Registrar("knav", "Knihovna akademie věd"));
		// 2013
		int current = 1000;
		for (int i = 0; i < 12; i++) {
			int month = i + 1;
			// current -= 10 * i;
			result.setMonthRegistrations(2013, month, current);
		}
		// 2014
		current = 2;
		for (int i = 0; i < 12; i++) {
			int month = i + 1;
			// current -= 10 * i;
			result.setMonthRegistrations(2014, month, current);
		}
		// 2015
		current = 3;
		for (int i = 0; i < 12; i++) {
			int month = i + 1;
			// current -= 10 * i;
			result.setMonthRegistrations(2015, month, current);
		}
		return result;
	}

	private static RegistrarRegistrationsData mzk() {
		RegistrarRegistrationsData result = new RegistrarRegistrationsData(
				new Registrar("mzk", "Moravská zemská knihovna"));
		// 2013
		int current = 2;
		for (int i = 0; i < 12; i++) {
			int month = i + 1;
			// current += 100 * i;
			result.setMonthRegistrations(2013, month, current);
		}
		// 2014
		for (int i = 0; i < 12; i++) {
			int month = i + 1;
			// current += 100 * i;
			result.setMonthRegistrations(2014, month, current);
		}
		// 2015
		for (int i = 0; i < 12; i++) {
			int month = i + 1;
			// current += 100 * i;
			result.setMonthRegistrations(2015, month, current);
		}
		return result;
	}

	private static RegistrarRegistrationsData nkp() {
		RegistrarRegistrationsData result = new RegistrarRegistrationsData(
				new Registrar("nkp", "Národní knihovna v Praze"));
		// 2013
		int current = 3;
		for (int i = 0; i < 12; i++) {
			int month = i + 1;
			// current += 10 * i;
			result.setMonthRegistrations(2013, month, current);
		}
		// 2014
		current = 2;
		for (int i = 0; i < 12; i++) {
			int month = i + 1;
			// current += 10 * i;
			result.setMonthRegistrations(2014, month, current);
		}
		// 2015
		current = 1;
		for (int i = 0; i < 12; i++) {
			int month = i + 1;
			// current += 10 * i;
			result.setMonthRegistrations(2015, month, current);
		}
		return result;
	}

}

package cz.nkp.urnnbn.server;

import cz.nkp.urnnbn.shared.Registrar;

public class RegistrarBuilder {

	public static Registrar buildMzk() {
		return new Registrar("mzk", "Moravská zemská knihovna");
	}

	public static Registrar buildNkp() {
		return new Registrar("nkp", "Národní knihovna v Praze");
	}

	public static Registrar buildKnav() {
		return new Registrar("knav", "Knihovna akademie věd");
	}

	public static Registrar buildRegistrar(int id) {
		return new Registrar("test_" + id, "Testovací registrátor " + id);
	}

	public static Registrar buildMuni() {
		return new Registrar("muni", "Masarykova univerzita");
	}

	public static Registrar buildNlk() {
		return new Registrar("nlk", "Národní lékařská knihovna");
	}

	public static Registrar buildOva() {
		return new Registrar("ova", "Ostravská univerzita v Ostravě - Univerzitní knihovna");
	}

}

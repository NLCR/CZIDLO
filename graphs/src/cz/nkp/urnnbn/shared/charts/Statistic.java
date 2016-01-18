package cz.nkp.urnnbn.shared.charts;

public class Statistic {

	public static enum Type {
		//TODO: prejmenovat preklepy
		URN_NBN_ASSIGNMENTS, URN_NBN_RESOLVATIONS;
	}

	public static enum Option {
		// URN_NBN_ASSIGNEMNTS
		URN_NBN_ASSIGNMENTS_INCLUDE_ACTIVE, //
		URN_NBN_ASSIGNMENTS_INCLUDE_DEACTIVATED;
		
		// URN_NBN_RESOLVATIONS
		// TODO
	}

}

package cz.nkp.urnnbn.server;

public class AssignmentsFactory {

	public static Assignments buildLinearAssignment(int yearStart, int yearEnd, int initialVolume, int monthlyIncreas) {
		Assignments result = new Assignments();
		int current = initialVolume;
		int totalIncrease = 0;
		for (int year = yearStart; year <= yearEnd; year++) {
			for (int month = 1; month <= 12; month++) {
				// current += 100 * i;
				result.setAssignments(year, month, current);
				totalIncrease+=monthlyIncreas;
				current = initialVolume + totalIncrease;
			}
		}
		return result;
	}

}

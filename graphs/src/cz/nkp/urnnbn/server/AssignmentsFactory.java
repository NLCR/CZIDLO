package cz.nkp.urnnbn.server;

public class AssignmentsFactory {

	public static Assignments buildLinearAssignment(int yearStart, int yearEnd, int initialVolume, int monthlyIncreas) {
		Assignments result = new Assignments();
		int current = initialVolume;
		int totalIncrease = 0;
		for (int year = yearStart; year <= yearEnd; year++) {
			for (int month = 1; month <= 12; month++) {
				result.setAssignments(year, month, current);
				totalIncrease += monthlyIncreas;
				current = initialVolume + totalIncrease;
			}
		}
		return result;
	}

	public static Assignments buildStaticPartOfYearAssignment(int year, int monthStart, int monthEnd, int volume) {
		Assignments result = new Assignments();
		for (int month = monthStart; month <= monthEnd; month++) {
			result.setAssignments(year, month, volume);
		}
		return result;
	}

}

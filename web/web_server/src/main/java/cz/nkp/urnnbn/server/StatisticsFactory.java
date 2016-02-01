package cz.nkp.urnnbn.server;

import java.util.Random;

public class StatisticsFactory {

    private static Random random = new Random();

    public static Statistics buildLinearAssignment(int yearStart, int yearEnd, int initialVolume, int monthlyIncreas) {
        Statistics result = new Statistics();
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

    public static Statistics buildRandomStatistics(int yearStart, int yearEnd, int monthlyMin, int monthlyMax, float yearlyGrowthRatio) {
        Statistics result = new Statistics();
        float currentYearlyGrowthRatio = 1;
        for (int year = yearStart; year <= yearEnd; year++) {
            for (int month = 1; month <= 12; month++) {
                int value = Math.max(0, (int) ((random.nextInt(monthlyMax - monthlyMin) + monthlyMin) * currentYearlyGrowthRatio));
                result.setAssignments(year, month, value);
            }
            currentYearlyGrowthRatio *= yearlyGrowthRatio;
        }
        return result;
    }

    public static Statistics buildStaticPartOfYearAssignment(int year, int monthStart, int monthEnd, int volume) {
        Statistics result = new Statistics();
        for (int month = monthStart; month <= monthEnd; month++) {
            result.setAssignments(year, month, volume);
        }
        return result;
    }

}

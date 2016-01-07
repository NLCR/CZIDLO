package cz.nkp.urnnbn.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Utils {

	public static Map<Integer, Map<String, Integer>> accumulate(List<Integer> periods, List<String> registrarCodes,
			Map<String, Integer> beforePeriodsData, Map<Integer, Map<String, Integer>> periodsData) {
		Map<Integer, Map<String, Integer>> result = new HashMap<Integer, Map<String, Integer>>();
		Integer previousPeriod = null;
		for (Integer period : periods) {
			Map<String, Integer> originalPeriodData = periodsData.get(period);
			Map<String, Integer> accumulatedPeriodData = new HashMap<String, Integer>();
			for (String registrarCode : registrarCodes) {
				Integer registrarPreviousPeriodsVolume = computePreviousPeriodsVolume(registrarCode, previousPeriod, result, beforePeriodsData);
				Integer registrarCurrentPeriodVolume = computeCurrentPeriodVolume(registrarCode, originalPeriodData);
				accumulatedPeriodData.put(registrarCode, registrarPreviousPeriodsVolume + registrarCurrentPeriodVolume);
			}
			result.put(period, accumulatedPeriodData);
			previousPeriod = period;
		}
		return result;
	}

	private static Integer computePreviousPeriodsVolume(String registrarCode, Integer previousPeriod,
			Map<Integer, Map<String, Integer>> accumulatedData, Map<String, Integer> volumeBeforeFirstPeriod) {
		if (previousPeriod == null) {
			if (volumeBeforeFirstPeriod != null) {
				Integer beforeFistPeriod = volumeBeforeFirstPeriod.get(registrarCode);
				return beforeFistPeriod != null ? beforeFistPeriod : 0;
			} else {
				return 0;
			}
		} else {
			Map<String, Integer> previousPeriodAccumulatedData = accumulatedData.get(previousPeriod);
			if (previousPeriodAccumulatedData != null) {
				Integer registrarPreviousPeriodsData = previousPeriodAccumulatedData.get(registrarCode);
				return registrarPreviousPeriodsData != null ? registrarPreviousPeriodsData : 0;
			} else {
				return 0;
			}
		}
	}

	private static Integer computeCurrentPeriodVolume(String registrarCode, Map<String, Integer> originalPeriodData) {
		if (originalPeriodData == null) {
			return 0;
		} else {
			Integer result = originalPeriodData.get(registrarCode);
			return result != null ? result : 0;
		}
	}

	public static List<String> extractAllRegistrarCodes(Map<Integer, Map<String, Integer>> data) {
		Set<String> set = new HashSet<String>();
		for (Map<String, Integer> map : data.values()) {
			for (String code : map.keySet()) {
				set.add(code);
			}
		}
		List<String> list = new ArrayList<>(set.size());
		list.addAll(set);
		return list;
	}

}

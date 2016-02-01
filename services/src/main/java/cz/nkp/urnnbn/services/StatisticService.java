package cz.nkp.urnnbn.services;

import java.util.Map;

public interface StatisticService extends BusinessService {

    /**
     * Increments resolvation statistics for specified registrar and current year and month.
     * 
     * @param registrarCode
     */
    public void incrementResolvationStatistics(String registrarCode);

    /**
     * @return first year in which some urn:nbn has been assigned or null.
     */
    public int getFirstYearWithData();

    /**
     * @return current year
     */
    public int getCurrentYear();

    /**
     * Returns urn:nbn assignment statistics for all registrars. Years are limited by first_year_any_urn:nbn_was_assigned and current_year. Or
     * <current_year;current_year> if no urn:nbn assigned yet.
     * 
     * @param includeActive
     *            if URN:NBNs that are currently active should be included
     * @param includeDeactivated
     *            if URN:NBNs that are now deactivated should be included
     * @return registrar_code -> year -> month -> asignments_in_year_and_month
     */
    public Map<String, Map<Integer, Map<Integer, Integer>>> getUrnNbnAssignmentStatistics(boolean includeActive, boolean includeDeactivated);

    /**
     * Returns urn:nbn assignment statistics for specified registrar. Years are limited by first_year_any_urn:nbn_was_assigned and current_year. Or
     * <current_year;current_year> if no urn:nbn assigned yet.
     * 
     * @param registrarCode
     * @param includeActive
     *            if URN:NBNs that are currently active should be included
     * @param includeDeactivated
     *            if URN:NBNs that are now deactivated should be included
     * @return year -> month -> asignments_in_year_and_month
     */
    public Map<Integer, Map<Integer, Integer>> getUrnNbnAssignmentStatistics(String registrarCode, boolean includeActive, boolean includeDeactivated);

    /**
     * Returns urn:nbn resolvation statistics for all registrars. Years are limited by first_year_any_urn:nbn_was_assigned and current_year. Or
     * <current_year;current_year> if no urn:nbn assigned yet.
     * 
     * @return registrar_code -> year -> month -> resolvations_in_year_and_month
     */
    public Map<String, Map<Integer, Map<Integer, Integer>>> getUrnNbnResolvationStatistics();

    /**
     * Returns urn:nbn resolvation statistics for specified registrar. Years are limited by first_year_any_urn:nbn_was_assigned and current_year. Or
     * <current_year;current_year> if no urn:nbn assigned yet.
     * 
     * @param registrarCode
     * @return year -> month -> resolvations_in_year_and_month
     */
    public Map<Integer, Map<Integer, Integer>> getUrnNbnResolvationStatistics(String registrarCode);

}

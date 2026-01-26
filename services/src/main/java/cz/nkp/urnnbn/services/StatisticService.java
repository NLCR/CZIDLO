package cz.nkp.urnnbn.services;

public interface StatisticService extends BusinessService {

    /**
     * Logs resolvation access for specified registrar and document and timestamp now.
     *
     * @param registrarCode
     * @param documentCode
     */
    public void logResolvationAccess(String registrarCode, String documentCode);



}

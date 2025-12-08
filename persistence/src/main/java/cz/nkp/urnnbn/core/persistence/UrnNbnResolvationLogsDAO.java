package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;

public interface UrnNbnResolvationLogsDAO {

    public String TABLE_RESOLVATIONS_NAME = "urnnbn_resolvation";
    public String ATTR_REGISTRAR_CODE = "registrarCode";
    public String ATTR_DOCUMENT_CODE = "documentCode";
    public String ATTR_RESOLVED = "resolved";

    public void insertResolvationAccessLog(String registrarCode, String documentCode) throws DatabaseException;

}

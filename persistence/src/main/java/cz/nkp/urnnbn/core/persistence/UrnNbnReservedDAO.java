/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import java.util.List;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;

/**
 *
 * @author Martin Řehánek
 */
public interface UrnNbnReservedDAO {

    public String TABLE_NAME = "UrnNbnReserved";
    public String ATTR_REGISTRAR_ID = "registrarId";
    public String ATTR_REGISTRAR_CODE = "registrarCode";
    public String ATTR_DOCUMENT_CODE = "documentCode";
    public String ATTR_CREATED = "created";

    public void insertUrnNbn(UrnNbn urn, long registrarId) throws DatabaseException, AlreadyPresentException, RecordNotFoundException;

    public UrnNbn getUrn(RegistrarCode code, String documentCode) throws DatabaseException, RecordNotFoundException;

    public void deleteUrn(UrnNbn urn) throws DatabaseException, RecordNotFoundException;

    public List<UrnNbn> getUrnNbnList(long registrarId) throws DatabaseException, RecordNotFoundException;
}

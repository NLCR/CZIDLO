/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.Sigla;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public interface RegistrarDAO {

    public String TABLE_NAME = "Registrar";
    public String ATTR_ID = "id";
    public String ATTR_URN_INST_CODE = "registrarCode";

    /**
     * Should not use id from registrar but instead create new by means of database
     * @param archiver
     * @return id created
     * @throws DatabaseException
     */
    public Long insertRegistrar(Registrar registrar) throws DatabaseException, AlreadyPresentException;

    public Registrar getRegistrarById(long id) throws DatabaseException, RecordNotFoundException;

    public Registrar getRegistrarBySigla(Sigla sigla) throws DatabaseException, RecordNotFoundException;

    public List<Registrar> getAllRegistrars() throws DatabaseException;

    public List<Long> getRegistrarsIdManagedByUser(long userId) throws DatabaseException, RecordNotFoundException;

    //urnRegistrarCode won't be updated
    public void updateRegistrar(Registrar registrar) throws DatabaseException, RecordNotFoundException;

    public void addAdminOfRegistrar(long registrarId, long userId) throws DatabaseException, RecordNotFoundException;

    public void activateRegistrar(long id) throws DatabaseException, RecordNotFoundException;

    public void deleteRegistrar(long id) throws DatabaseException, RecordNotFoundException;

    public void deleteAllRegistrars() throws DatabaseException;
}

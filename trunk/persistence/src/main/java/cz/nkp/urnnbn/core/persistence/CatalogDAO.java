/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public interface CatalogDAO {

    public String TABLE_NAME = "Catalogue";
    public String SEQ_NAME = "seq_Catalogue";
    public String ATTR_ID = "id";
    public String ATTR_REG_ID = "registrarId";
    public String ATTR_NAME = "name";
    public String ATTR_DESC = "description";
    public String ATTR_URL_PREFIX = "urlPrefix";

    public Long insertCatalog(Catalog catalog) throws DatabaseException, RecordNotFoundException, AlreadyPresentException;

    public Catalog getCatalogById(long id) throws DatabaseException, RecordNotFoundException;

    public List<Catalog> getCatalogs(long rgistrarId) throws DatabaseException, RecordNotFoundException;

    public List<Catalog> getCatalogs() throws DatabaseException;

    public void updateCatalog(Catalog catalog) throws DatabaseException, RecordNotFoundException;

    public void deleteCatalog(long catalogId) throws DatabaseException, RecordNotFoundException;
}

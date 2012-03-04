/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public class CatalogDaoPostgresTest extends AbstractDaoTest {

    public CatalogDaoPostgresTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of insertCatalog method, of class CatalogDaoPostgres.
     */
    public void testInsertCatalog() throws Exception {
        Registrar registrar = registrarPersisted();
        Catalog catalog = builder.catalogWithoutIdAndRegistrarId();
        catalog.setRegistrarId(registrar.getId());
        catalogDao.insertCatalog(catalog);
    }

    public void testInsertCatalog_unknownRegistrarId() throws Exception {
        Catalog catalog = builder.catalogWithoutIdAndRegistrarId();
        catalog.setRegistrarId(ILLEGAL_ID);
        try {
            catalogDao.insertCatalog(catalog);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    public void testGetCatalog() throws Exception {
        Registrar registrar = registrarPersisted();
        Catalog inserted = builder.catalogWithoutIdAndRegistrarId();
        inserted.setRegistrarId(registrar.getId());
        catalogDao.insertCatalog(inserted);
        Catalog fetched = catalogDao.getCatalogById(inserted.getId());
        assertEquals(inserted, fetched);
    }

    public void testGetCatalog_byUnknownId() throws Exception {
        try {
            catalogDao.getCatalogById(ILLEGAL_ID);
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    /**
     * Test of getCatalogs method, of class CatalogDaoPostgres.
     */
    public void testGetCatalogsByRegistrarId() throws Exception {
        Registrar registrar = registrarPersisted();
        //first catalog
        Catalog first = builder.catalogWithoutIdAndRegistrarId();
        first.setRegistrarId(registrar.getId());
        catalogDao.insertCatalog(first);
        //second catalog
        Catalog second = builder.catalogWithoutIdAndRegistrarId();
        second.setRegistrarId(registrar.getId());
        catalogDao.insertCatalog(second);
        //third catalog (but of another registrar)
        Catalog third = builder.catalogWithoutIdAndRegistrarId();
        third.setRegistrarId(registrarPersisted().getId());
        catalogDao.insertCatalog(third);
        //get catalogs of registrar
        List<Catalog> catalogs = catalogDao.getCatalogs(registrar.getId());
        assertEquals(2, catalogs.size());
        assertTrue(catalogs.contains(first));
        assertTrue(catalogs.contains(second));
        assertFalse(catalogs.contains(third));
    }

    public void testGetCatalogs() throws Exception {
        assertTrue(catalogDao.getCatalogs().isEmpty());

        //first catalog
        Catalog first = builder.catalogWithoutIdAndRegistrarId();
        first.setRegistrarId(registrarPersisted().getId());
        catalogDao.insertCatalog(first);
        assertEquals(1, catalogDao.getCatalogs().size());

        //second catalog
        Catalog second = builder.catalogWithoutIdAndRegistrarId();
        second.setRegistrarId(registrarPersisted().getId());
        catalogDao.insertCatalog(second);
        assertEquals(2, catalogDao.getCatalogs().size());
    }

    public void testGetCatalogsList_noCatalogs() throws Exception {
        Registrar registrar = registrarPersisted();
        List<Catalog> catalogs = catalogDao.getCatalogs(registrar.getId());
        assertEquals(0, catalogs.size());
    }

    public void testGetCatalogsList_unknownRegistrar() throws Exception {
        try {
            catalogDao.getCatalogs(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    /**
     * Test of updateCatalog method, of class CatalogDaoPostgres.
     */
    public void testUpdateCatalog() throws Exception {
        Registrar registrar = registrarPersisted();
        Catalog inserted = builder.catalogWithoutIdAndRegistrarId();
        inserted.setRegistrarId(registrar.getId());
        catalogDao.insertCatalog(inserted);
        Catalog updated = new Catalog(inserted);
        updated.setName(inserted.getName() + "-new");
        updated.setUrlPrefix(inserted.getUrlPrefix() + "/new");
        catalogDao.updateCatalog(updated);
        Catalog fetched = catalogDao.getCatalogById(inserted.getId());
        assertEquals(fetched, updated);
        assertFalse(fetched.equals(inserted));
    }

    public void testUpdateCatalog_unknownRegistrarId() throws Exception {
        Registrar registrar = registrarPersisted();
        Catalog inserted = builder.catalogWithoutIdAndRegistrarId();
        inserted.setRegistrarId(registrar.getId());
        catalogDao.insertCatalog(inserted);
        Catalog updated = new Catalog(inserted);
        updated.setRegistrarId(ILLEGAL_ID);
        catalogDao.updateCatalog(updated);
        Catalog fetched = catalogDao.getCatalogById(inserted.getId());
        assertEquals(fetched, inserted);
        assertFalse(fetched.equals(updated));
    }

    /**
     * Test of deleteCatalog method, of class CatalogDaoPostgres.
     */
    public void testDeleteCatalog() throws Exception {
        Registrar registrar = registrarPersisted();
        Catalog catalog = builder.catalogWithoutIdAndRegistrarId();
        catalog.setRegistrarId(registrar.getId());
        catalogDao.insertCatalog(catalog);
        catalogDao.deleteCatalog(catalog.getId());
        try {
            catalogDao.getCatalogById(catalog.getId());
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }

    public void testDeleteCatalog_unknownId() throws Exception {
        try {
            catalogDao.deleteCatalog(ILLEGAL_ID);
            fail();
        } catch (RecordNotFoundException e) {
            //ok
        }
    }
}

package cz.nkp.urnnbn.server.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.nkp.urnnbn.client.services.InstitutionsService;
import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.server.dtoTransformation.DtoToArchiverTransformer;
import cz.nkp.urnnbn.server.dtoTransformation.DtoToCatalogTransformer;
import cz.nkp.urnnbn.server.dtoTransformation.DtoToDigitalLibraryTransformer;
import cz.nkp.urnnbn.server.dtoTransformation.DtoToRegistrarTransformer;
import cz.nkp.urnnbn.server.dtoTransformation.DtoTransformer;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;
import cz.nkp.urnnbn.shared.dto.CatalogDTO;
import cz.nkp.urnnbn.shared.dto.DigitalLibraryDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

public class InstitutionsServiceImpl extends AbstractService implements InstitutionsService {

    private static final long serialVersionUID = 1860087889450503955L;
    private static final Logger logger = Logger.getLogger(InstitutionsServiceImpl.class.getName());

    @Override
    public ArchiverDTO saveArchiver(ArchiverDTO archiver) throws ServerException {
        try {
            checkNotReadOnlyMode();
            Archiver created = createService.insertNewArchiver(new DtoToArchiverTransformer(archiver).transform(), getUserLogin());
            return DtoTransformer.transformArchiver(created);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public RegistrarDTO saveRegistrar(RegistrarDTO registrar) throws ServerException {
        try {
            checkNotReadOnlyMode();
            Registrar created = createService.insertNewRegistrar(new DtoToRegistrarTransformer(registrar).transform(), getUserLogin());
            return DtoTransformer.transformRegistrar(created);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public DigitalLibraryDTO saveDigitalLibrary(DigitalLibraryDTO dto) throws ServerException {
        try {
            checkNotReadOnlyMode();
            DigitalLibrary transformed = new DtoToDigitalLibraryTransformer(dto).transform();
            DigitalLibrary created = createService.insertNewDigitalLibrary(transformed, dto.getRegistrarId(), getUserLogin());
            return DtoTransformer.transformDigitalLibrary(created);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public CatalogDTO saveCatalog(CatalogDTO dto) throws ServerException {
        try {
            checkNotReadOnlyMode();
            if (dto.getRegistrarId() == null) {
                throw new Exception("registrarId == null");
            }
            Catalog transformed = new DtoToCatalogTransformer(dto).transform();
            Catalog created = createService.insertNewCatalog(transformed, dto.getRegistrarId(), getUserLogin());
            return DtoTransformer.transformCatalog(created);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public ArrayList<DigitalLibraryDTO> getLibraries(Long registrarId) throws ServerException {
        try {
            List<DigitalLibrary> libraries = readService.librariesByRegistrarId(registrarId);
            return transformLibraries(libraries);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    private ArrayList<DigitalLibraryDTO> transformLibraries(List<DigitalLibrary> libraries) {
        ArrayList<DigitalLibraryDTO> result = new ArrayList<DigitalLibraryDTO>(libraries.size());
        for (DigitalLibrary original : libraries) {
            result.add(DtoTransformer.transformDigitalLibrary(original));
        }
        return result;
    }

    @Override
    public ArrayList<CatalogDTO> getCatalogs(Long registrarId) throws ServerException {
        try {
            List<Catalog> catalogs = readService.catalogsByRegistrarId(registrarId);
            return transformCatalogs(catalogs);
        } catch (Exception e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }

    }

    private ArrayList<CatalogDTO> transformCatalogs(List<Catalog> catalogs) {
        ArrayList<CatalogDTO> result = new ArrayList<CatalogDTO>(catalogs.size());
        for (Catalog original : catalogs) {
            result.add(DtoTransformer.transformCatalog(original));
        }
        return result;
    }

    @Override
    public ArrayList<RegistrarDTO> getAllRegistrars() throws ServerException {
        try {
            List<Registrar> registrars = readService.registrars();
            return transformRegistrars(registrars);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    private ArrayList<RegistrarDTO> transformRegistrars(List<Registrar> registrars) {
        ArrayList<RegistrarDTO> result = new ArrayList<RegistrarDTO>(registrars.size());
        for (Registrar original : registrars) {
            RegistrarDTO transformed = DtoTransformer.transformRegistrar(original);
            result.add(transformed);
        }
        return result;
    }

    @Override
    public ArrayList<ArchiverDTO> getAllArchivers() throws ServerException {
        try {
            List<Archiver> archivers = readService.archivers();
            return transformArchivers(archivers);
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    private ArrayList<ArchiverDTO> transformArchivers(List<Archiver> archivers) {
        ArrayList<ArchiverDTO> result = new ArrayList<ArchiverDTO>(archivers.size());
        for (Archiver archiver : archivers) {
            result.add(DtoTransformer.transformArchiver(archiver));
        }
        return result;
    }

    @Override
    public void updateRegistrar(RegistrarDTO registrar) throws ServerException {
        try {
            checkNotReadOnlyMode();
            Registrar transformed = new DtoToRegistrarTransformer(registrar).transform();
            updateService.updateRegistrar(transformed, getUserLogin());
            updateService.updateArchiver(transformed, getUserLogin());
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public void updateRegistrars(List<RegistrarDTO> registrars) throws ServerException {
        try {
            checkNotReadOnlyMode();
            for (RegistrarDTO registrar : registrars) {
                Registrar transformed = new DtoToRegistrarTransformer(registrar).transform();
                updateService.updateRegistrar(transformed, getUserLogin());
                updateService.updateArchiver(transformed, getUserLogin());
            }
        } catch (Throwable e) {
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public void updateArchiver(ArchiverDTO archiver) throws ServerException {
        try {
            checkNotReadOnlyMode();
            Archiver transformed = new DtoToArchiverTransformer(archiver).transform();
            updateService.updateArchiver(transformed, getUserLogin());
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public void updateArchivers(List<ArchiverDTO> archivers) throws ServerException {
        try {
            checkNotReadOnlyMode();
            for (ArchiverDTO archiver : archivers) {
                Archiver transformed = new DtoToArchiverTransformer(archiver).transform();
                updateService.updateArchiver(transformed, getUserLogin());
            }
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public void updateDigitalLibrary(DigitalLibraryDTO dto) throws ServerException {
        try {
            checkNotReadOnlyMode();
            DigitalLibrary transformed = new DtoToDigitalLibraryTransformer(dto).transform();
            updateService.updateDigitalLibrary(transformed, getUserLogin());
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public void updateCatalog(CatalogDTO dto) throws ServerException {
        try {
            checkNotReadOnlyMode();
            Catalog transformed = new DtoToCatalogTransformer(dto).transform();
            updateService.updateCatalog(transformed, getUserLogin());
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public void deleteArchiver(ArchiverDTO archiver) throws ServerException {
        try {
            checkNotReadOnlyMode();
            deleteService.removeArchiver(archiver.getId(), getUserLogin());
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public void deleteRegistrar(RegistrarDTO registrar) throws ServerException {
        try {
            checkNotReadOnlyMode();
            deleteService.removeRegistrar(registrar.getId(), getUserLogin());
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public void deleteDigitalLibrary(DigitalLibraryDTO lib) throws ServerException {
        try {
            checkNotReadOnlyMode();
            deleteService.removeDigitalLibrary(lib.getId(), getUserLogin());
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

    @Override
    public void deleteCatalog(CatalogDTO cat) throws ServerException {
        try {
            checkNotReadOnlyMode();
            deleteService.removeCatalog(cat.getId(), getUserLogin());
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }
}

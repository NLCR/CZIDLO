package cz.nkp.urnnbn.client.services;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.nkp.urnnbn.shared.dto.ArchiverDTO;
import cz.nkp.urnnbn.shared.dto.CatalogDTO;
import cz.nkp.urnnbn.shared.dto.DigitalLibraryDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

@RemoteServiceRelativePath("institutions")
public interface InstitutionsService extends RemoteService {

	ArchiverDTO saveArchiver(ArchiverDTO archiver) throws ServerException;

	RegistrarDTO saveRegistrar(RegistrarDTO registrar) throws ServerException;

	DigitalLibraryDTO saveDigitalLibrary(DigitalLibraryDTO dto) throws ServerException;

	CatalogDTO saveCatalog(CatalogDTO dto) throws ServerException;

	ArrayList<RegistrarDTO> getAllRegistrars() throws ServerException;

	ArrayList<ArchiverDTO> getAllArchivers() throws ServerException;

	ArrayList<DigitalLibraryDTO> getLibraries(Long registrarId) throws ServerException;

	ArrayList<CatalogDTO> getCatalogs(Long registrarId) throws ServerException;

	void updateRegistrar(RegistrarDTO registrar) throws ServerException;

	void updateArchiver(ArchiverDTO registrar) throws ServerException;

	void updateDigitalLibrary(DigitalLibraryDTO dto) throws ServerException;

	void updateCatalog(CatalogDTO dto) throws ServerException;

	void deleteArchiver(ArchiverDTO archiver) throws ServerException;

	void deleteRegistrar(RegistrarDTO registrar) throws ServerException;

	void deleteDigitalLibrary(DigitalLibraryDTO lib) throws ServerException;

	void deleteCatalog(CatalogDTO cat) throws ServerException;
}

package cz.nkp.urnnbn.client.services;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import cz.nkp.urnnbn.shared.dto.ArchiverDTO;
import cz.nkp.urnnbn.shared.dto.CatalogDTO;
import cz.nkp.urnnbn.shared.dto.DigitalLibraryDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;

public interface InstitutionsServiceAsync {

    void saveArchiver(ArchiverDTO archiver, AsyncCallback<ArchiverDTO> callback);

    void saveRegistrar(RegistrarDTO registrar, AsyncCallback<RegistrarDTO> callback);

    void saveDigitalLibrary(DigitalLibraryDTO dto, AsyncCallback<DigitalLibraryDTO> asyncCallback);

    void saveCatalog(CatalogDTO dto, AsyncCallback<CatalogDTO> asyncCallback);

    void getAllRegistrars(AsyncCallback<ArrayList<RegistrarDTO>> callback);

    void getAllArchivers(AsyncCallback<ArrayList<ArchiverDTO>> callback);

    void getLibraries(Long registrarId, AsyncCallback<ArrayList<DigitalLibraryDTO>> callback);

    void getCatalogs(Long registrarId, AsyncCallback<ArrayList<CatalogDTO>> callback);

    void updateRegistrar(RegistrarDTO registrar, AsyncCallback<Void> callback);

    void updateRegistrars(List<RegistrarDTO> registrars, AsyncCallback<Void> callback);

    void updateArchiver(ArchiverDTO archiver, AsyncCallback<Void> callback);

    void updateArchivers(List<ArchiverDTO> archivers, AsyncCallback<Void> callback);

    void updateDigitalLibrary(DigitalLibraryDTO dto, AsyncCallback<Void> asyncCallback);

    void updateCatalog(CatalogDTO dto, AsyncCallback<Void> asyncCallback);

    void deleteArchiver(ArchiverDTO archiver, AsyncCallback<Void> callback);

    void deleteRegistrar(RegistrarDTO registrar, AsyncCallback<Void> callback);

    void deleteDigitalLibrary(DigitalLibraryDTO lib, AsyncCallback<Void> asyncCallback);

    void deleteCatalog(CatalogDTO cat, AsyncCallback<Void> asyncCallback);
}

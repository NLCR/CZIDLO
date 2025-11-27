package cz.nkp.urnnbn.czidlo_web_api.api.registrars.registrar_manager;

import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.BadArgumentException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.DuplicateRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Catalogue;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.DigitalLibrary;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Registrar;

import java.util.List;

public interface RegistrarManager {

    /**
     * Creates a registrar.
     *
     * @param login                                login of user creating the registrar
     * @param registrarCode                        a unique code of the registrar
     * @param name                                 unique name of the registrar
     * @param allowedRegistrationModeByResolver    if the registrar is a resolver
     * @param allowedRegistrationModeByReservation if the registrar is a reserver
     * @param allowedRegistrationModeByRegistrar   if the registrar is a registrar //TODO: unknown meanings of params
     * @param description                          description of the registrar
     * @return instance of the registrar
     * @throws DuplicateRecordException if a registrar of that name already exists
     */
    public Registrar createRegistrar(String login, String registrarCode, String name, String description, boolean allowedRegistrationModeByResolver, boolean allowedRegistrationModeByReservation, boolean allowedRegistrationModeByRegistrar) throws DuplicateRecordException;

    /**
     * Returns a registrar with code.
     *
     * @param registrarCode code of the registrar
     * @return instance of the registrar
     * @throws UnknownRecordException if a registrar with that code does not exist
     * @throws BadArgumentException   if registrarCode is invalid
     */
    public Registrar getRegistrarByCode(String registrarCode) throws UnknownRecordException, BadArgumentException;

    /**
     * Returns all registrars.
     *
     * @return list of all registrars
     */
    public List<Registrar> getRegistrars();

    /// Updates a registrar.
    ///
    /// @param login                                login of user updating the registrar
    /// @param registrarCode                        a unique code of the registrar
    /// @param name                                 unique name of the registrar
    /// @param description                          description of the registrar
    /// @param allowedRegistrationModeByResolver    if the registrar is a resolver
    /// @param allowedRegistrationModeByReservation if the registrar is a reserver
    /// @param allowedRegistrationModeByRegistrar   if the registrar is a registrar //TODO: unknown meanings of params
    /// @param isHidden                             if the registrar is hidden
    /// @return instance of the registrar
    /// @throws UnknownRecordException   if a registrar with that ID does not exist
    /// @throws DuplicateRecordException if a registrar of that name already exists
    public Registrar updateRegistrar(String login, String registrarCode, String name, String description, boolean allowedRegistrationModeByResolver, boolean allowedRegistrationModeByReservation, boolean allowedRegistrationModeByRegistrar, boolean isHidden) throws UnknownRecordException, DuplicateRecordException;

    /**
     * Creates a library for a registrar.
     *
     * @param login         login of user adding the library
     * @param registrarCode a unique code of the registrar
     * @param name          a name of the library
     * @param description   description of the library
     * @param url           the url of the library
     * @throws UnknownRecordException if a registrar with that code does not exist
     */
    public DigitalLibrary createLibrary(String login, String registrarCode, String name, String description, String url) throws UnknownRecordException;

    /**
     * Updates a library in a registrar.
     *
     * @param login         login of user adding the library
     * @param registrarCode a unique code of the registrar
     * @param libraryId     id of the library
     * @param name          a name of the library
     * @param description   description of the library
     * @param url           the url of the library
     * @throws UnknownRecordException if a registrar or a library with that code does not exist
     */
    public DigitalLibrary updateLibrary(String login, String registrarCode, long libraryId, String name, String description, String url) throws UnknownRecordException;

    /**
     * Deletes a library from a registrar.
     *
     * @param login         login of user adding the library
     * @param registrarCode a unique code of the registrar
     * @param libraryId     id of the library
     * @throws UnknownRecordException if a registrar with that code does not exist
     */
    public void deleteLibrary(String login, String registrarCode, long libraryId) throws UnknownRecordException;

    /**
     * Creates a catalogue for a registrar.
     *
     * @param login         login of user adding the catalogue
     * @param registrarCode a unique code of the registrar
     * @param name          a name of the catalogue
     * @param description   description of the catalogue
     * @param urlPrefix     the url prefix of the catalogue
     * @throws UnknownRecordException if a registrar with that code does not exist
     */
    public Catalogue createCatalogue(String login, String registrarCode, String name, String description, String urlPrefix) throws UnknownRecordException;

    /**
     * Updates a catalogue in a registrar.
     *
     * @param login         login of user adding the catalogue
     * @param registrarCode a unique code of the registrar
     * @param catalogueId   id of the catalogue
     * @param name          a name of the catalogue
     * @param description   description of the catalogue
     * @param urlPrefix     the url of the catalogue
     * @throws UnknownRecordException if a registrar or a catalogue with that code does not exist
     */
    public Catalogue updateCatalogue(String login, String registrarCode, long catalogueId, String name, String description, String urlPrefix) throws UnknownRecordException;

    /**
     * Deletes a catalogue from a registrar.
     *
     * @param login         login of user adding the catalogue
     * @param registrarCode a unique code of the registrar
     * @param catalogueId   id of the catalogue
     * @throws UnknownRecordException if a registrar or a catalogue with that code does not exist
     */
    public void deleteCatalogue(String login, String registrarCode, long catalogueId) throws UnknownRecordException;


    /**
     * Deletes a registrar.
     *
     * @param login         login of user deleting the registrar
     * @param registrarCode a unique code of the registrar
     * @throws UnknownRecordException if a registrar with that code does not exist
     */
    public void deleteRegistrar(String login, String registrarCode) throws UnknownRecordException;
}


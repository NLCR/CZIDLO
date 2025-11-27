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
     * @param login                                login of user performing this operation
     * @param registrarCode                        a unique code of the registrar
     * @param name                                 name of the registrar
     * @param allowedRegistrationModeByResolver    if the registrar should be allowed to register in mode BY_RESOLVER
     * @param allowedRegistrationModeByReservation if the registrar should be allowed to register in mode BY_RESERVATION
     * @param allowedRegistrationModeByRegistrar   if the registrar should be allowed to register in mode BY_REGISTRAR
     * @param description                          description of the registrar
     * @return newly created registrar
     * @throws BadArgumentException     if registrarCode is invalid
     * @throws DuplicateRecordException if a registrar of that name already exists
     */
    public Registrar createRegistrar(String login, String registrarCode, String name, String description,
                                     boolean allowedRegistrationModeByResolver, boolean allowedRegistrationModeByReservation, boolean allowedRegistrationModeByRegistrar)
            throws BadArgumentException, DuplicateRecordException;

    /**
     * Returns a registrar with code.
     *
     * @param registrarCode code of the registrar
     * @return registrar with given code
     * @throws UnknownRecordException if a registrar with that code does not exist
     * @throws BadArgumentException   if registrarCode is invalid
     */
    public Registrar getRegistrarByCode(String registrarCode)
            throws UnknownRecordException, BadArgumentException;

    /**
     * Returns all registrars.
     *
     * @return list of all registrars
     */
    public List<Registrar> getRegistrars();

    /**
     * Updates a registrar.
     *
     * @param login                                login of user performing this operation
     * @param registrarCode                        a unique code of the registrar
     * @param name                                 name of the registrar
     * @param description                          description of the registrar
     * @param allowedRegistrationModeByResolver    if the registrar should be allowed to register in mode BY_RESOLVER
     * @param allowedRegistrationModeByReservation if the registrar should be allowed to register in mode BY_RESERVATION
     * @param allowedRegistrationModeByRegistrar   if the registrar should be allowed to register in mode BY_REGISTRAR
     * @param isHidden                             if the registrar is hidden
     * @return registrar after update
     * @throws UnknownRecordException   if a registrar with that code does not exist
     * @throws DuplicateRecordException if a registrar with this code already exists
     * @throws BadArgumentException     if registrarCode is invalid
     */
    public Registrar updateRegistrar(String login, String registrarCode, String name, String description,
                                     boolean allowedRegistrationModeByResolver, boolean allowedRegistrationModeByReservation, boolean allowedRegistrationModeByRegistrar,
                                     boolean isHidden) throws UnknownRecordException, DuplicateRecordException, BadArgumentException;

    /**
     * Deletes a registrar.
     *
     * @param login         login of user performing this operation
     * @param registrarCode unique code of the registrar
     * @throws UnknownRecordException if a registrar with that code does not exist
     * @throws BadArgumentException   if registrarCode is invalid
     */
    public void deleteRegistrar(String login, String registrarCode)
            throws UnknownRecordException, BadArgumentException;


    /**
     * Creates a library for a registrar.
     *
     * @param login         login of user performing this operation
     * @param registrarCode code of the registrar owning the library
     * @param name          a name of the library
     * @param description   description of the library
     * @param url           the url of the library
     * @return newly created library
     * @throws UnknownRecordException if a registrar with that code does not exist
     */
    public DigitalLibrary createLibrary(String login, String registrarCode, String name, String description, String url)
            throws UnknownRecordException;

    /**
     * Updates a library in a registrar.
     *
     * @param login         login of user performing this operation
     * @param registrarCode code of the registrar owning the library
     * @param libraryId     id of the library being updated
     * @param name          a name of the library
     * @param description   description of the library
     * @param url           the url of the library
     * @return updated library
     * @throws UnknownRecordException if a registrar or a library with that code does not exist
     * @throws BadArgumentException   if registrarCode is invalid
     */
    public DigitalLibrary updateLibrary(String login, String registrarCode, long libraryId, String name, String description, String url)
            throws UnknownRecordException, BadArgumentException;

    /**
     * Deletes a library from a registrar.
     *
     * @param login         login of user performing this operation
     * @param registrarCode code of the registrar owning the library
     * @param libraryId     id of the library being deleted
     * @throws UnknownRecordException if a registrar or a library with that code does not exist
     * @throws BadArgumentException   if registrarCode is invalid
     */
    public void deleteLibrary(String login, String registrarCode, long libraryId)
            throws UnknownRecordException, BadArgumentException;

    /**
     * Creates a catalogue for a registrar.
     *
     * @param login         login of user performing this operation
     * @param registrarCode code of the registrar owning the catalogue
     * @param name          a name of the catalogue
     * @param description   description of the catalogue
     * @param urlPrefix     the url prefix of the catalogue
     * @return newly created catalogue
     * @throws UnknownRecordException if a registrar with that code does not exist
     * @throws BadArgumentException   if registrarCode is invalid
     */
    public Catalogue createCatalogue(String login, String registrarCode, String name, String description, String urlPrefix)
            throws UnknownRecordException, BadArgumentException;

    /**
     * Updates a catalogue in a registrar.
     *
     * @param login         login of user performing this operation
     * @param registrarCode code of the registrar owning the catalogue
     * @param catalogueId   id of the catalogue being updated
     * @param name          a name of the catalogue
     * @param description   description of the catalogue
     * @param urlPrefix     the url of the catalogue
     * @return updated catalogue
     * @throws UnknownRecordException if a registrar or a catalogue with that code does not exist
     * @throws BadArgumentException   if registrarCode is invalid
     */
    public Catalogue updateCatalogue(String login, String registrarCode, long catalogueId, String name, String description, String urlPrefix)
            throws UnknownRecordException, BadArgumentException;

    /**
     * Deletes a catalogue from a registrar.
     *
     * @param login         login of user performing this operation
     * @param registrarCode code of the registrar owning the catalogue
     * @param catalogueId   id of the catalogue being deleted
     * @throws UnknownRecordException if a registrar or a catalogue with that code does not exist
     * @throws BadArgumentException   if registrarCode is invalid
     */
    public void deleteCatalogue(String login, String registrarCode, long catalogueId)
            throws UnknownRecordException, BadArgumentException;


}


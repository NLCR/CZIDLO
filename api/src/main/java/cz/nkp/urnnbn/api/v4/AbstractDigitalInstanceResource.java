package cz.nkp.urnnbn.api.v4;

import java.util.logging.Level;

import cz.nkp.urnnbn.api.v4.exceptions.DigitalInstanceAlreadyDeactivatedException;
import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigInstException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalDocumentBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalInstanceBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalLibraryBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.RegistrarBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.RegistrarScopeIdentifiersBuilder;

public abstract class AbstractDigitalInstanceResource extends ApiV4Resource {

    private final DigitalInstance instance;

    public AbstractDigitalInstanceResource(DigitalInstance instance) {
        this.instance = instance;
    }

    public abstract String getDigitalInstanceXmlRecord();

    public final String getDigitalInstanceApiV4XmlRecord() {
        return digitalInstanceXmlBuilder(instance, true, true).buildDocumentWithResponseHeader().toXML();
    }

    private DigitalInstanceBuilder digitalInstanceXmlBuilder(DigitalInstance instance, boolean withDigDoc, boolean withDigLib) {
        DigitalDocumentBuilder digDocBuilder = withDigDoc ? digDocBuilder(instance.getDigDocId()) : null;
        DigitalLibraryBuilder libBuilder = withDigLib ? digLibBuilder(instance.getLibraryId()) : null;
        return new DigitalInstanceBuilder(instance, libBuilder, digDocBuilder);
    }

    private DigitalDocumentBuilder digDocBuilder(long digDocId) {
        DigitalDocument digDoc = dataAccessService().digDocByInternalId(digDocId);
        UrnNbn urn = dataAccessService().urnByDigDocId(digDoc.getId(), true);
        RegistrarScopeIdentifiersBuilder idsBuilder = registrarScopeIdentifiersBuilder(digDocId);
        return new DigitalDocumentBuilder(digDoc, urn, idsBuilder, null, null, null, null);
    }

    private DigitalLibraryBuilder digLibBuilder(long libraryId) {
        DigitalLibrary library = dataAccessService().libraryByInternalId(libraryId);
        Registrar registrar = dataAccessService().registrarById(library.getRegistrarId());
        RegistrarBuilder regBuilder = new RegistrarBuilder(registrar, null, null);
        return new DigitalLibraryBuilder(library, regBuilder);
    }

    protected final String deactivateDigitalInstance(String login) {
        DigitalInstance found = dataAccessService().digInstanceByInternalId(instance.getId());
        if (!found.isActive()) {
            throw new DigitalInstanceAlreadyDeactivatedException(instance);
        } else {
            deactivateDigitalInstanceWithServiceExceptionTranslation(login);
            DigitalInstance deactivated = dataAccessService().digInstanceByInternalId(instance.getId());
            DigitalInstanceBuilder builder = new DigitalInstanceBuilder(deactivated, deactivated.getLibraryId());
            return builder.buildDocumentWithResponseHeader().toXML();
        }
    }

    private void deactivateDigitalInstanceWithServiceExceptionTranslation(String login) {
        try {
            dataRemoveService().deactivateDigitalInstance(instance.getId(), login);
        } catch (UnknownUserException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (UnknownDigInstException ex) {
            // should never happen
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex);
        }
    }

}

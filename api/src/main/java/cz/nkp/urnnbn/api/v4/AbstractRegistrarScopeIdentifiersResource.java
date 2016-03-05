package cz.nkp.urnnbn.api.v4;

import java.util.List;
import java.util.logging.Level;

import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidRegistrarScopeIdentifier;
import cz.nkp.urnnbn.api.v4.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.api.v4.exceptions.NotDefinedException;
import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.RegistrarScopeIdValue;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.IdentifierConflictException;
import cz.nkp.urnnbn.services.exceptions.RegistrarScopeIdentifierNotDefinedException;
import cz.nkp.urnnbn.services.exceptions.UnknownDigDocException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.xml.apiv4.builders.RegistrarScopeIdentifierBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.RegistrarScopeIdentifiersBuilder;

public abstract class AbstractRegistrarScopeIdentifiersResource extends ApiV4Resource {
    private final DigitalDocument doc;

    public AbstractRegistrarScopeIdentifiersResource(DigitalDocument doc) {
        this.doc = doc;
    }

    public abstract String getRegistrarScopeIdentifiersXmlRecord();

    protected final String getRegistrarScopeIdentifiersApiV4XmlRecord() {
        RegistrarScopeIdentifiersBuilder builder = registrarScopeIdentifiersBuilder(doc.getId());
        return builder.buildDocumentWithResponseHeader().toXML();
    }

    public abstract String getRegistrarScopeIdentifierValue(String idTypeStr);

    protected final String getRegistrarScopeIdentifierValueApiV4XmlRecord(RegistrarScopeIdType idType) {
        List<RegistrarScopeIdentifier> identifiers = dataAccessService().registrarScopeIdentifiers(doc.getId());
        for (RegistrarScopeIdentifier id : identifiers) {
            if (id.getType().equals(idType)) {
                RegistrarScopeIdentifierBuilder builder = new RegistrarScopeIdentifierBuilder(id);
                return builder.buildDocumentWithResponseHeader().toXML();
            }
        }
        throw new NotDefinedException(idType);
    }

    protected final RegistrarScopeIdentifier presentIdentifierOrNull(RegistrarScopeIdType idType) {
        try {
            return dataAccessService().registrarScopeIdentifier(doc.getId(), idType);
        } catch (RegistrarScopeIdentifierNotDefinedException ex) {
            return null;
        }
    }

    protected final RegistrarScopeIdentifier addNewIdentifier(RegistrarScopeIdType type, RegistrarScopeIdValue value, String login) {
        try {
            RegistrarScopeIdentifier newId = identifierInstance(type, value);
            dataImportService().addRegistrarScopeIdentifier(newId, login);
            return newId;
        } catch (UnknownUserException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (UnknownRegistrarException ex) {
            // should never happen here
            logger.log(Level.SEVERE, null, ex);
            throw new InternalException(ex);
        } catch (UnknownDigDocException ex) {
            // should never happen here
            logger.log(Level.SEVERE, null, ex);
            throw new InternalException(ex);
        } catch (IdentifierConflictException ex) {
            // should never happen here
            logger.log(Level.SEVERE, null, ex);
            throw new InvalidRegistrarScopeIdentifier(ex.getMessage());
        }
    }

    protected final RegistrarScopeIdentifier updateIdentifier(String login, RegistrarScopeIdType type, RegistrarScopeIdValue value) {
        try {
            RegistrarScopeIdentifier id = identifierInstance(type, value);
            dataUpdateService().updateRegistrarScopeIdentifier(login, id);
            return id;
        } catch (UnknownUserException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (UnknownRegistrarException ex) {
            // should never happen here
            logger.log(Level.SEVERE, null, ex);
            throw new InternalException(ex);
        } catch (UnknownDigDocException ex) {
            // should never happen here
            logger.log(Level.SEVERE, null, ex);
            throw new InternalException(ex);
        } catch (IdentifierConflictException ex) {
            // should never happen here
            logger.log(Level.SEVERE, null, ex);
            throw new InvalidRegistrarScopeIdentifier(ex.getMessage());
        }
    }

    private RegistrarScopeIdentifier identifierInstance(RegistrarScopeIdType type, RegistrarScopeIdValue value) {
        RegistrarScopeIdentifier result = new RegistrarScopeIdentifier();
        result.setDigDocId(doc.getId());
        result.setRegistrarId(doc.getRegistrarId());
        result.setType(type);
        result.setValue(value);
        return result;
    }

    protected final String deleteRegistrarScopeIdentifierWithApiV4Response(String login, String idTypeStr) {
        try {
            RegistrarScopeIdType idType = Parser.parseRegistrarScopeIdType(idTypeStr);
            RegistrarScopeIdentifier identifier = dataAccessService().registrarScopeIdentifier(doc.getId(), idType);
            dataRemoveService().removeRegistrarScopeIdentifier(doc.getId(), idType, login);
            RegistrarScopeIdentifierBuilder builder = new RegistrarScopeIdentifierBuilder(identifier);
            return builder.buildDocumentWithResponseHeader().toXML();
        } catch (UnknownUserException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (UnknownDigDocException ex) {
            // should never happen
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        } catch (RegistrarScopeIdentifierNotDefinedException ex) {
            throw new InvalidRegistrarScopeIdentifier(ex.getMessage());
        }
    }

    protected final String deleteAllRegistrarScopeIdentifiersWithApiV4Response(String login) {
        try {
            RegistrarScopeIdentifiersBuilder builder = registrarScopeIdentifiersBuilder(doc.getId());
            dataRemoveService().removeRegistrarScopeIdentifiers(doc.getId(), login);
            return builder.buildDocumentWithResponseHeader().toXML();
        } catch (UnknownUserException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (UnknownDigDocException ex) {
            // should never happen
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

}

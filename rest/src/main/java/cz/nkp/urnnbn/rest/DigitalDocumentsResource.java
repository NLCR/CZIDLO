/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.DigDocIdType;
import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.UrnNbnWithStatus.Status;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.rest.exceptions.IncorrectPredecessorException;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.rest.exceptions.InvalidArchiverIdException;
import cz.nkp.urnnbn.rest.exceptions.InvalidDigDocIdentifier;
import cz.nkp.urnnbn.rest.exceptions.InvalidUrnException;
import cz.nkp.urnnbn.rest.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.rest.exceptions.UnauthorizedRegistrationModeException;
import cz.nkp.urnnbn.rest.exceptions.UnknownDigitalDocumentException;
import cz.nkp.urnnbn.services.DigDocRegistrationData;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.RegistarScopeDigDocIdentifierCollisionException;
import cz.nkp.urnnbn.services.exceptions.UnknownArchiverException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.services.exceptions.UrnNotFromRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UrnUsedException;
import cz.nkp.urnnbn.xml.builders.DigitalDocumentsBuilder;
import cz.nkp.urnnbn.xml.builders.UrnNbnBuilder;
import cz.nkp.urnnbn.xml.unmarshallers.RecordImportUnmarshaller;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import nu.xom.Document;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalDocumentsResource extends Resource {

    private static final String PARAM_URN = "urn";
    @Context
    private UriInfo context;
    private final Registrar registrar;

    /**
     * Creates a new instance of RegistrarsResource
     */
    public DigitalDocumentsResource(Registrar registrar) {
        this.registrar = registrar;
    }

    @GET
    @Produces("application/xml")
    public String getDigitalDocuments() {
        try {
            int digRepCount = dataAccessService().digitalDocumentsCount(registrar.getId());
            DigitalDocumentsBuilder builder = new DigitalDocumentsBuilder(digRepCount);
            return builder.buildDocument().toXML();
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public String registerDigitalDocument(@Context HttpServletRequest req, String content,
            @QueryParam(PARAM_URN) String urnParam,
            @QueryParam(value = "predUrn") final List<String> predecessors) {
        String login = req.getRemoteUser();
        try {
            Document doc = validDocumentFromString(content, ApiModuleConfiguration.instanceOf().getRecordImportSchema());
            DigDocRegistrationData registrationData = digDocRegistrationDataFromDoc(doc);
            registrationData.setUrn(urnNbnFromParam(urnParam));
            if (!predecessors.isEmpty()) {
                registrationData.setPredecessors(predecessorsFromParams(predecessors));
            }
            UrnNbn urn = dataImportService().registerDigitalDocument(registrationData, login);
            UrnNbnWithStatus withStatus = urnWithStatus(urn, true);
            UrnNbnBuilder builder = new UrnNbnBuilder(withStatus);
            return builder.buildDocument().toXML();
        } catch (UnknownUserException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (UnknownArchiverException ex) {
            throw new InvalidArchiverIdException(ex.getMessage());
        } catch (RegistarScopeDigDocIdentifierCollisionException ex) {
            throw new InvalidDigDocIdentifier(ex.getMessage());
        } catch (UrnNotFromRegistrarException ex) {
            throw new InvalidUrnException(ex.getUrn().toString(), ex.getMessage());
        } catch (UrnUsedException ex) {
            throw new InvalidUrnException(ex.getUrn().toString(), ex.getMessage());
        } catch (UnknownRegistrarException ex) {
            logger.log(Level.SEVERE, "unexpected application state", ex);
            throw new InternalException(ex);
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "unexpected application state", e);
            if (e instanceof WebApplicationException) {
                throw e;
            } else {
                throw new InternalException(e);
            }
        }
    }

    /**
     * Sub-resource locator method for {id}
     */
    @Path("id/{idType}/{idValue}")
    public DigitalDocumentResource getDigitalDocumentResource(
            @PathParam("idType") String idTypeStr,
            @PathParam("idValue") String idValue) {
        try {
            DigDocIdType type = Parser.parseDigRepIdType(idTypeStr);
            DigDocIdentifier id = new DigDocIdentifier();
            id.setRegistrarId(registrar.getId());
            id.setType(type);
            id.setValue(idValue);
            DigitalDocument digRep = dataAccessService().digDocByIdentifier(id);
            if (digRep == null) {
                throw new UnknownDigitalDocumentException(registrar.getCode(), type, idValue);
            }
            return new DigitalDocumentResource(digRep, null);
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    private DigDocRegistrationData digDocRegistrationDataFromDoc(Document doc) {
        RecordImportUnmarshaller unmarshaller = new RecordImportUnmarshaller(doc);
        DigDocRegistrationData result = new DigDocRegistrationData();
        //intelectual entity
        result.setEntity(unmarshaller.getIntelectualEntity());
        result.setIntEntIds(unmarshaller.getIntEntIdentifiers());
        result.setOriginator(unmarshaller.getOriginator());
        result.setPublication(unmarshaller.getPublication());
        result.setOriginator(unmarshaller.getOriginator());
        result.setSourceDoc(unmarshaller.getSourceDocument());
        //registrar        
        result.setRegistrarCode(registrar.getCode());
        //archiver
        Long archiverId = unmarshaller.getArchiverId() == null
                ? registrar.getId() : unmarshaller.getArchiverId();
        //digital document
        DigitalDocument digDoc = unmarshaller.getDigitalDocument();
        digDoc.setRegistrarId(registrar.getId());
        digDoc.setArchiverId(archiverId);
        result.setDigitalDocument(digDoc);
        result.setDigDocIdentifiers(unmarshaller.getDigRepIdentifiers());
        return result;
    }

    private UrnNbn urnNbnFromParam(String urnParam) throws InvalidUrnException, UnauthorizedRegistrationModeException {
        if (urnParam == null || urnParam.isEmpty()) {
            if (!registrar.isRegistrationModeAllowed(UrnNbnRegistrationMode.BY_RESOLVER)) {
                throw new UnauthorizedRegistrationModeException("Mode " + UrnNbnRegistrationMode.BY_RESOLVER + " not allowed for registrar " + registrar);
            } else {
                return null;
            }
        }

        //this is duplicated - same functionality in RecordImporter
        //also method isReserved
        //requires refactoring
        UrnNbn urnNbn = UrnNbn.valueOf(urnParam);
        if (!urnNbn.getRegistrarCode().equals(registrar.getCode())) {
            throw new InvalidUrnException(urnNbn.toString(), "doesn't match registrar code " + registrar.getCode().toString());
        }
        Status status = urnWithStatus(urnNbn, false).getStatus();
        System.err.println("STATUS: " + status);
        if (status == Status.ACTIVE) {
            throw new InvalidUrnException(urnNbn.toString(), " already active");
        }
        if (status == Status.DEACTIVATED) {
            throw new InvalidUrnException(urnNbn.toString(), " has been deactivated");
        }
        if (status == Status.RESERVED && !registrar.isRegistrationModeAllowed(UrnNbnRegistrationMode.BY_RESERVATION)) {
            throw new UnauthorizedRegistrationModeException("Mode " + UrnNbnRegistrationMode.BY_RESERVATION + " not allowed for registrar " + registrar);
        }
        if (status == Status.FREE && !registrar.isRegistrationModeAllowed(UrnNbnRegistrationMode.BY_REGISTRAR)) {
            throw new UnauthorizedRegistrationModeException("Mode " + UrnNbnRegistrationMode.BY_REGISTRAR + " not allowed for registrar " + registrar);
        }
        return urnNbn;
    }

    private UrnNbnWithStatus urnWithStatus(UrnNbn urn, boolean withPredecessorsAndSuccessors) {
        try {
            return dataAccessService().urnByRegistrarCodeAndDocumentCode(urn.getRegistrarCode(), urn.getDocumentCode(), withPredecessorsAndSuccessors);
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<UrnNbnWithStatus> predecessorsFromParams(List<String> predecessorParams) {
        List<UrnNbnWithStatus> predecessorList = new ArrayList<UrnNbnWithStatus>(predecessorParams.size());
        for (String predecessor : predecessorParams) {
            UrnNbn urnNbn = UrnNbn.valueOf(predecessor);
            UrnNbnWithStatus withStatus = urnWithStatus(urnNbn, false);
            Status status = withStatus.getStatus();
            if (status == Status.RESERVED || status == Status.FREE) {
                throw new IncorrectPredecessorException(withStatus);
            }
            predecessorList.add(withStatus);
        }
        return predecessorList;
    }
}

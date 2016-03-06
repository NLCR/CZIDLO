/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.nkp.urnnbn.api.v4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.v4.exceptions.IncorrectPredecessorException;
import cz.nkp.urnnbn.api.v4.exceptions.InternalException;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidArchiverIdException;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidDataException;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidRegistrarScopeIdentifier;
import cz.nkp.urnnbn.api.v4.exceptions.InvalidUrnException;
import cz.nkp.urnnbn.api.v4.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.api.v4.exceptions.UnauthorizedRegistrationModeException;
import cz.nkp.urnnbn.api.v4.exceptions.UnknownDigitalDocumentException;
import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.RegistrarScopeIdValue;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.services.DigDocRegistrationData;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.IncorrectPredecessorStatus;
import cz.nkp.urnnbn.services.exceptions.RegistarScopeIdentifierCollisionException;
import cz.nkp.urnnbn.services.exceptions.RegistrationModeNotAllowedException;
import cz.nkp.urnnbn.services.exceptions.UnknownArchiverException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.services.exceptions.UrnNotFromRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UrnUsedException;
import cz.nkp.urnnbn.xml.apiv4.builders.DigitalDocumentsBuilder;
import cz.nkp.urnnbn.xml.apiv4.builders.UrnNbnBuilder;
import cz.nkp.urnnbn.xml.apiv4.unmarshallers.RecordImportUnmarshaller;

public class DigitalDocumentsResource extends ApiV4Resource {

    private static final Logger LOGGER = Logger.getLogger(DigitalDocumentsResource.class.getName());

    private final Registrar registrar;

    public DigitalDocumentsResource(Registrar registrar) {
        this.registrar = registrar;
    }

    @Path("registrarScopeIdentifier/{idType}/{idValue}")
    public DigitalDocumentResource getDigitalDocumentResource(@PathParam("idType") String idTypeStr, @PathParam("idValue") String idValueStr) {
        try {
            LOGGER.log(Level.INFO, "resolving registrar-scope id (type=''{0}'', value=''{1}'') for registrar {2}", new Object[] { idTypeStr,
                    idValueStr, registrar.getCode() });
            DigitalDocument digitalDocument = getDigitalDocument(idTypeStr, idValueStr);
            UrnNbn urn = dataAccessService().urnByDigDocId(digitalDocument.getId(), true);
            return new DigitalDocumentResource(digitalDocument, urn);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    private DigitalDocument getDigitalDocument(String idTypeStr, String idValueStr) {
        RegistrarScopeIdType type = Parser.parseRegistrarScopeIdType(idTypeStr);
        RegistrarScopeIdValue value = Parser.parseRegistrarScopeIdValue(idValueStr);
        RegistrarScopeIdentifier id = new RegistrarScopeIdentifier();
        id.setRegistrarId(registrar.getId());
        id.setType(type);
        id.setValue(value);
        DigitalDocument digDoc = dataAccessService().digDocByIdentifier(id);
        if (digDoc == null) {
            throw new UnknownDigitalDocumentException(registrar.getCode(), type, value);
        } else {
            return digDoc;
        }
    }

    @GET
    @Produces("application/xml")
    public String getDigitalDocumentsRecord() {
        try {
            return getDigitalDocumentsRecordXml();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    private String getDigitalDocumentsRecordXml() {
        int digDocsCount = dataAccessService().digitalDocumentsCount(registrar.getId());
        DigitalDocumentsBuilder builder = new DigitalDocumentsBuilder(digDocsCount);
        return builder.buildDocumentWithResponseHeader().toXML();
    }

    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public Response registerDigitalDocument(@Context HttpServletRequest req, String content) {
        try {
            checkServerNotReadOnly();
            String login = req.getRemoteUser();
            String response = registerDigitalDocumentReturnXml(content, login, registrar.getCode());
            return Response.created(null).entity(response).build();
        } catch (ValidityException ex) {
            throw new InvalidDataException(ex);
        } catch (ParsingException ex) {
            throw new InvalidDataException(ex);
        } catch (WebApplicationException e) {
            throw e;
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            throw new InternalException(e);
        }
    }

    private String registerDigitalDocumentReturnXml(String content, String login, RegistrarCode registrarCode) throws ValidityException, IOException,
            ParsingException {
        Document doc = ApiModuleConfiguration.instanceOf().getDigDocRegistrationDataValidatingLoaderV4().loadDocument(content);
        return registerDigitalDocumentReturnXml(doc, login, registrarCode);
    }

    private String registerDigitalDocumentReturnXml(Document doc, String login, RegistrarCode registrarCode) {
        try {
            DigDocRegistrationData registrationData = digDocRegistrationDataFromDoc(doc);
            UrnNbn urnInData = registrationData.getUrn();
            if (urnInData != null && !urnInData.getRegistrarCode().toString().equals(registrarCode.toString())) {
                throw new InvalidUrnException(urnInData.toString(), "Doesn't match expected registrar code '" + registrarCode.toString() + "'");
            }
            UrnNbn urn = dataImportService().registerDigitalDocument(registrationData, login);
            UrnNbnWithStatus withStatus = getUrnWithStatus(urn, true);
            UrnNbnBuilder builder = new UrnNbnBuilder(withStatus);
            return builder.buildDocumentWithResponseHeader().toXML();
        } catch (IncorrectPredecessorStatus ex) {
            throw new IncorrectPredecessorException(ex.getPredecessor());
        } catch (RegistrationModeNotAllowedException ex) {
            throw new UnauthorizedRegistrationModeException(ex.getMode(), ex.getUrn(), registrar);
        } catch (UnknownUserException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (UnknownArchiverException ex) {
            throw new InvalidArchiverIdException(ex.getMessage());
        } catch (RegistarScopeIdentifierCollisionException ex) {
            throw new InvalidRegistrarScopeIdentifier(ex.getMessage());
        } catch (UrnNotFromRegistrarException ex) {
            throw new InvalidUrnException(ex.getUrn().toString(), ex.getMessage());
        } catch (UrnUsedException ex) {
            throw new InvalidUrnException(ex.getUrn().toString(), ex.getMessage());
        } catch (UnknownRegistrarException ex) {
            LOGGER.log(Level.SEVERE, "unexpected application state", ex);
            throw new InternalException(ex);
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        }
    }

    private DigDocRegistrationData digDocRegistrationDataFromDoc(Document doc) {
        RecordImportUnmarshaller unmarshaller = new RecordImportUnmarshaller(doc);
        DigDocRegistrationData result = new DigDocRegistrationData();
        // intelectual entity
        result.setEntity(unmarshaller.getIntelectualEntity());
        result.setIntEntIds(unmarshaller.getIntEntIdentifiers());
        result.setOriginator(unmarshaller.getOriginator());
        result.setPublication(unmarshaller.getPublication());
        result.setOriginator(unmarshaller.getOriginator());
        result.setSourceDoc(unmarshaller.getSourceDocument());
        // registrar
        result.setRegistrarCode(registrar.getCode());
        // archiver
        Long archiverId = unmarshaller.getArchiverId() == null ? registrar.getId() : unmarshaller.getArchiverId();
        // digital document
        DigitalDocument digDoc = unmarshaller.getDigitalDocument();
        digDoc.setRegistrarId(registrar.getId());
        digDoc.setArchiverId(archiverId);
        result.setDigitalDocument(digDoc);
        result.setDigDocIdentifiers(unmarshaller.getRegistrarScopeIdentifiers());
        // urn:nbn
        result.setUrn(unmarshaller.getUrnNbn());
        // predecessors
        result.setPredecessors(appendStatuses(unmarshaller.getPredecessors()));
        return result;
    }

    private UrnNbnWithStatus getUrnWithStatus(UrnNbn urn, boolean withPredecessorsAndSuccessors) {
        return dataAccessService().urnByRegistrarCodeAndDocumentCode(urn.getRegistrarCode(), urn.getDocumentCode(), withPredecessorsAndSuccessors);
    }

    private List<UrnNbnWithStatus> appendStatuses(List<UrnNbnWithStatus> predecessors) {
        List<UrnNbnWithStatus> result = new ArrayList<UrnNbnWithStatus>(predecessors.size());
        for (UrnNbnWithStatus urn : predecessors) {
            UrnNbnWithStatus withCorrectStatus = getUrnWithStatus(urn.getUrn(), false);
            result.add(new UrnNbnWithStatus(withCorrectStatus.getUrn(), withCorrectStatus.getStatus(), urn.getNote()));
        }
        return result;
    }

}

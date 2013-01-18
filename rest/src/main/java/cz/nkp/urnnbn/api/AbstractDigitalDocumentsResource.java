/*
 * Copyright (C) 2013 Martin Řehánek
 *
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
package cz.nkp.urnnbn.api;

import cz.nkp.urnnbn.api.config.ApiModuleConfiguration;
import cz.nkp.urnnbn.api.exceptions.IncorrectPredecessorException;
import cz.nkp.urnnbn.api.exceptions.InternalException;
import cz.nkp.urnnbn.api.exceptions.InvalidArchiverIdException;
import cz.nkp.urnnbn.api.exceptions.InvalidRegistrarScopeIdentifier;
import cz.nkp.urnnbn.api.exceptions.InvalidUrnException;
import cz.nkp.urnnbn.api.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.api.exceptions.UnauthorizedRegistrationModeException;
import cz.nkp.urnnbn.api.exceptions.UnknownDigitalDocumentException;
import cz.nkp.urnnbn.api.v3.DigitalDocumentResource;
import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.services.DigDocRegistrationData;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.RegistarScopeIdentifierCollisionException;
import cz.nkp.urnnbn.services.exceptions.RegistrationModeNotAllowedException;
import cz.nkp.urnnbn.services.exceptions.UnknownArchiverException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UnknownUserException;
import cz.nkp.urnnbn.services.exceptions.UrnNotFromRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UrnUsedException;
import cz.nkp.urnnbn.xml.builders.DigitalDocumentsBuilder;
import cz.nkp.urnnbn.xml.builders.UrnNbnBuilder;
import cz.nkp.urnnbn.xml.unmarshallers.RecordImportUnmarshaller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.WebApplicationException;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 *
 * @author Martin Řehánek
 */
public abstract class AbstractDigitalDocumentsResource extends Resource {

    protected final Registrar registrar;

    public AbstractDigitalDocumentsResource(Registrar registrar) {
        this.registrar = registrar;
    }

    public String getDigitalDocuments() {
        try {
            int digDocsCount = dataAccessService().digitalDocumentsCount(registrar.getId());
            DigitalDocumentsBuilder builder = new DigitalDocumentsBuilder(digDocsCount);
            return builder.buildDocumentWithResponseHeader().toXML();
        } catch (WebApplicationException e) {
            throw e;
        } catch (RuntimeException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    protected String registerDigitalDocumentByApiV3(String content, String login) throws ValidityException, IOException, ParsingException {
        Document doc = ApiModuleConfiguration.instanceOf().getDigDocRegistrationDataValidatingLoaderV3().loadDocument(content);
        return registerDigitalDocumentByApiV3(doc, login);
    }

    protected String registerDigitalDocumentByApiV3(Document doc, String login) {
        try {
            DigDocRegistrationData registrationData = digDocRegistrationDataFromDoc(doc);
            UrnNbn urn = dataImportService().registerDigitalDocument(registrationData, login);
            UrnNbnWithStatus withStatus = urnWithStatus(urn, true);
            UrnNbnBuilder builder = new UrnNbnBuilder(withStatus);
            return builder.buildDocumentWithResponseHeader().toXML();
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
            logger.log(Level.SEVERE, "unexpected application state", ex);
            throw new InternalException(ex);
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (WebApplicationException e) {
            throw e;
        } catch (RuntimeException ex) {
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
        result.setDigDocIdentifiers(unmarshaller.getRegistrarScopeIdentifiers());
        //urn:nbn
        result.setUrn(unmarshaller.getUrnNbn());
        //predecessors
        result.setPredecessors(appendStatuses(unmarshaller.getPredecessors()));
        return result;
    }

    protected UrnNbnWithStatus urnWithStatus(UrnNbn urn, boolean withPredecessorsAndSuccessors) {
        return dataAccessService().urnByRegistrarCodeAndDocumentCode(urn.getRegistrarCode(), urn.getDocumentCode(), withPredecessorsAndSuccessors);
    }

    public DigitalDocumentResource getDigitalDocumentResource(String idTypeStr, String idValue) {
        try {
            logger.log(Level.INFO, "resolving registrar-scope id (type=''{0}'', value=''{1}'') for registrar {2}", new Object[]{idTypeStr, idValue, registrar.getCode()});
            RegistrarScopeIdType type = Parser.parseRegistrarScopeIdType(idTypeStr);
            RegistrarScopeIdentifier id = new RegistrarScopeIdentifier();
            id.setRegistrarId(registrar.getId());
            id.setType(type);
            id.setValue(idValue);
            DigitalDocument digDoc = dataAccessService().digDocByIdentifier(id);
            if (digDoc == null) {
                throw new UnknownDigitalDocumentException(registrar.getCode(), type, idValue);
            }
            return new DigitalDocumentResource(digDoc, null);
        } catch (WebApplicationException e) {
            throw e;
        } catch (RuntimeException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    //    private UrnNbn urnNbnFromParam(String urnParam) throws InvalidUrnException, UnauthorizedRegistrationModeException {
//        //System.err.println("urnNbnFromParam");
//        if (urnParam == null || urnParam.isEmpty()) {
//            if (!registrar.isRegistrationModeAllowed(UrnNbnRegistrationMode.BY_RESOLVER)) {
//                throw new UnauthorizedRegistrationModeException("Mode " + UrnNbnRegistrationMode.BY_RESOLVER + " not allowed for registrar " + registrar);
//            } else {
//                return null;
//            }
//        }
//
//        //this is duplicated - same functionality in RecordImporter
//        //also method isReserved
//        //requires refactoring
//        UrnNbn urnNbn = UrnNbn.valueOf(urnParam);
//        if (!urnNbn.getRegistrarCode().equals(registrar.getCode())) {
//            throw new InvalidUrnException(urnNbn.toString(), "doesn't match registrar code " + registrar.getCode().toString());
//        }
//        Status status = urnWithStatus(urnNbn, false).getStatus();
//        if (status == Status.ACTIVE) {
//            throw new InvalidUrnException(urnNbn.toString(), " already active");
//        }
//        if (status == Status.DEACTIVATED) {
//            throw new InvalidUrnException(urnNbn.toString(), " has been deactivated");
//        }
//        if (status == Status.RESERVED && !registrar.isRegistrationModeAllowed(UrnNbnRegistrationMode.BY_RESERVATION)) {
//            throw new UnauthorizedRegistrationModeException("Mode " + UrnNbnRegistrationMode.BY_RESERVATION + " not allowed for registrar " + registrar);
//        }
//        if (status == Status.FREE && !registrar.isRegistrationModeAllowed(UrnNbnRegistrationMode.BY_REGISTRAR)) {
//            throw new UnauthorizedRegistrationModeException("Mode " + UrnNbnRegistrationMode.BY_REGISTRAR + " not allowed for registrar " + registrar);
//        }
//        return urnNbn;
//    }
    public List<UrnNbnWithStatus> predecessorsFromParams(List<String> predecessorParams) {
        List<UrnNbnWithStatus> predecessorList = new ArrayList<UrnNbnWithStatus>(predecessorParams.size());
        for (String predecessor : predecessorParams) {
            UrnNbn urnNbn = UrnNbn.valueOf(predecessor);
            UrnNbnWithStatus withStatus = urnWithStatus(urnNbn, false);
            UrnNbnWithStatus.Status status = withStatus.getStatus();
            if (status == UrnNbnWithStatus.Status.RESERVED || status == UrnNbnWithStatus.Status.FREE) {
                throw new IncorrectPredecessorException(withStatus);
            }
            predecessorList.add(withStatus);
        }
        return predecessorList;
    }

    private List<UrnNbnWithStatus> appendStatuses(List<UrnNbnWithStatus> predecessors) {
        List<UrnNbnWithStatus> result = new ArrayList<UrnNbnWithStatus>(predecessors.size());
        for (UrnNbnWithStatus urn : predecessors) {
            UrnNbnWithStatus withCorrectStatus = urnWithStatus(urn.getUrn(), false);
            result.add(new UrnNbnWithStatus(withCorrectStatus.getUrn(), withCorrectStatus.getStatus(), urn.getNote()));
        }
        return result;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest;

import cz.nkp.urnnbn.core.DigRepIdType;
import cz.nkp.urnnbn.core.Sigla;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.DigRepIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalRepresentation;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.rest.exceptions.InternalException;
import cz.nkp.urnnbn.rest.exceptions.NotAuthorizedException;
import cz.nkp.urnnbn.rest.exceptions.UnknownDigitalRepresentationException;
import cz.nkp.urnnbn.services.RecordImport;
import cz.nkp.urnnbn.services.exceptions.AccessException;
import cz.nkp.urnnbn.services.exceptions.ImportFailedException;
import cz.nkp.urnnbn.services.exceptions.UnknownRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UrnNotFromRegistrarException;
import cz.nkp.urnnbn.services.exceptions.UrnUsedException;
import cz.nkp.urnnbn.xml.builders.DigitalRepresentationsBuilder;
import cz.nkp.urnnbn.xml.builders.UrnNbnBuilder;
import cz.nkp.urnnbn.xml.unmarshallers.RecordImportUnmarshaller;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import nu.xom.Document;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalRepresentationsResource extends Resource {

    @Context
    private UriInfo context;
    private final Registrar registrar;

    /** Creates a new instance of RegistrarsResource */
    public DigitalRepresentationsResource(Registrar registrar) {
        this.registrar = registrar;
    }

    /**
     * Retrieves representation of an instance of cz.nkp.urnnbn.rest.RegistrarsResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/xml")
    public String getDigitalRepresentations() {
        try {
            int digRepCount = dataAccessService().digitalRepresentationsCount(registrar.getId());
            DigitalRepresentationsBuilder builder = new DigitalRepresentationsBuilder(digRepCount);
            return builder.buildDocument().toXML();
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    /**
     * POST method for creating an instance of RegistrarResource
     * @param content representation for the new resource
     * @return an HTTP response with content of the created resource
     */
    @POST
    @Consumes("application/xml")
    @Produces("application/xml")
    public String importDigitalRepresentation(String content) {
        //todo: autentizace
        long userId = 1;//TODO: ziskat z hlavicky
        try {

            Document doc = validDocumentFromString(content, Config.RECORD_IMPORT_XSD);
            RecordImport recordImport = getImportFromDocument(doc);
            UrnNbn urn = dataImportService().importNewRecord(recordImport, userId);
            UrnNbnWithStatus withStatus = new UrnNbnWithStatus(urn, UrnNbnWithStatus.Status.ACTIVE);
            UrnNbnBuilder builder = new UrnNbnBuilder(withStatus);
            return builder.buildDocument().toXML();
        } catch (UrnNotFromRegistrarException ex) {
            //TODO
            Logger.getLogger(DigitalRepresentationsResource.class.getName()).log(Level.SEVERE, null, ex);
            return "</TODO>";
        } catch (UrnUsedException ex) {
            //TODO
            Logger.getLogger(DigitalRepresentationsResource.class.getName()).log(Level.SEVERE, null, ex);
            return "</TODO>";
        } catch (UnknownRegistrarException ex) {
            //TODO
            Logger.getLogger(DigitalRepresentationsResource.class.getName()).log(Level.SEVERE, null, ex);
            return "</TODO>";
        } catch (DatabaseException ex) {
            throw new InternalException(ex.getMessage());
        } catch (AccessException ex) {
            throw new NotAuthorizedException(ex.getMessage());
        } catch (ImportFailedException ex) {
            if (ex.getMessage()!= null){
                logger.log(Level.SEVERE, ex.getMessage());
            }
            throw new InternalException(ex);
        } catch (RuntimeException e) {
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
    public DigitalRepresentationResource getDigitalRepresentationResource(
            @PathParam("idType") String idTypeStr,
            @PathParam("idValue") String idValue) {
        try {
            DigRepIdType type = parseDigRepIdType(idTypeStr);
            DigRepIdentifier id = new DigRepIdentifier();
            id.setRegistrarId(registrar.getId());
            id.setType(type);
            id.setValue(idValue);
            DigitalRepresentation digRep = dataAccessService().digRepByIdentifier(id);
            if (digRep == null) {
                throw new UnknownDigitalRepresentationException(registrar.getUrnInstitutionCode(), type, idValue);
            }
            return new DigitalRepresentationResource(digRep, null);
        } catch (DatabaseException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new InternalException(ex.getMessage());
        }
    }

    private RecordImport getImportFromDocument(Document doc) {
        RecordImportUnmarshaller unmarshaller = new RecordImportUnmarshaller(doc);
        RecordImport result = new RecordImport();
        //intelectual entity
        result.setEntity(unmarshaller.getIntelectualEntity());
        result.setIntEntIds(unmarshaller.getIntEntIdentifiers());
        result.setOriginator(unmarshaller.getOriginator());
        result.setPublication(unmarshaller.getPublication());
        result.setOriginator(unmarshaller.getOriginator());
        result.setSourceDoc(unmarshaller.getSourceDocument());
        //digital representation
        result.setRepresentation(unmarshaller.getDigitalRepresentation());
        result.setDigRepIds(unmarshaller.getDigRepIdentifiers());
        result.setUrn(unmarshaller.getUrnNbn());
        //registrar        
        Sigla sigla = Sigla.valueOf(registrar.getUrnInstitutionCode());
        result.setRegistrarSigla(sigla);
        //archiver
        Long archiverId = unmarshaller.getArchiverId();
        if (archiverId == null) {
            result.setArchiverId(archiverId);
        } else {
            result.setArchiverId(registrar.getId());
        }
        return result;
    }
}

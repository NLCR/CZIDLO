package cz.nkp.urnnbn.czidlo_web_api.api.resources;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.czidlo_web_api.api.ApiError;
import cz.nkp.urnnbn.czidlo_web_api.api.AuthenticatedUserPrincipal;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.BadArgumentException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.InsufficientRightsException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnauthorizedException;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Registrar;
import cz.nkp.urnnbn.processmanager.core.XmlTransformation;
import cz.nkp.urnnbn.processmanager.core.XmlTransformationType;
import cz.nkp.urnnbn.processmanager.persistence.UnknownRecordException;
import cz.nkp.urnnbn.processmanager.persistence.XmlTransformationDAO;
import cz.nkp.urnnbn.processmanager.persistence.XmlTransformationDAOImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.springframework.security.core.parameters.P;

import javax.persistence.PostRemove;
import java.io.StringReader;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

//see TransformationsResource
/*
 @see cz.nkp.urnnbn.processdataserver.TransformationsResource
 @see cz.nkp.urnnbn.processdataserver.TransformationResource
 @see cz.nkp.urnnbn.processdataserver.TransformationXmlBuilder
 */

@Path("/process_oai_adapter")
public class ProcessOaiAdapterResource extends AbstractResource {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX'['VV']'").withZone(ZoneId.of("UTC"));

    @Context
    private SecurityContext securityContext;

    private XmlTransformationDAO xmlTransformationDao = XmlTransformationDAOImpl.instanceOf();

    @Operation(
            summary = "Create new transformation for OAI-PMH adapter",
            tags = {"Processes", "OAI-Adapter"},
            description = "Creates a new transformation record (without xslt yet)",
            responses = {
                    @ApiResponse(
                            responseCode = "201", description = "Transformation created",
                            content = @Content(schema = @Schema(implementation = TransformationResult.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @POST
    @Path("transformations")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTransformation(@RequestBody(
            content = @Content(schema = @Schema(implementation = TransformationCreate.class)),
            description = "JSON object representing new transformation",
            required = true
    ) String body) throws UnauthorizedException, BadArgumentException {
        //authorization: must be logged in
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();

        //parse mandatory body to json
        if (body == null || body.isEmpty()) {
            return mandatoryBodyMissingResponse();
        }
        JsonObject root;
        try (JsonReader r = Json.createReader(new StringReader(body))) {
            root = r.readObject();
        }
        //name
        String name = readParam("name", root::getString);
        if (name.isEmpty()) {
            throw new BadArgumentException("Transformation name cannot be empty");
        }
        //type
        XmlTransformationType type = parseTransformationType(root, "type");
        //description
        String desc = null;
        if (root.containsKey("description")) {
            desc = readParam("description", root::getString);
        }
        //xslt - initially empty
        String xslt = "EMPTY";

        XmlTransformation newTransformation = new XmlTransformation();
        newTransformation.setName(name);
        newTransformation.setDescription(desc);
        newTransformation.setType(type);
        newTransformation.setOwnerLogin(user.getLogin());
        newTransformation.setXslt(xslt);
        XmlTransformation transformationCreated = xmlTransformationDao.saveTransformation(newTransformation);
        //respond
        return Response.status(Response.Status.CREATED)
                .entity(toTransformationResult(transformationCreated))
                .build();
    }

    @Operation(
            summary = "Retrieve transformation for OAI-PMH adapter",
            tags = {"Processes", "OAI-Adapter"},
            description = "Returns transformation for OAI-PMH adapter process without the XSLT content",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Transformation retrieved",
                            content = @Content(schema = @Schema(implementation = TransformationResult.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid transformation ID format",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing authentication",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights (user is not the owner of the transformation or admin)",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GET
    @Path("transformations/{id}")
    public Response getTransformation(@PathParam("id") String id) throws UnauthorizedException, InsufficientRightsException, cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException {
        //authorization: must be logged in
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        //fetch transformation
        XmlTransformation transformationFetched = getTransformationById(id);
        //check rights: only owner or admin can access
        if (!transformationFetched.getOwnerLogin().equals(user.getLogin()) && !user.isAdmin()) {
            throw new InsufficientRightsException("Only admin or owner can access the transformation");
        }
        //respond
        return Response.ok()
                .entity(toTransformationResult(transformationFetched))
                .build();
    }

    @Operation(
            summary = "Retrieve transformation XSLT (for OAI-PMH adapter)",
            tags = {"Processes", "OAI-Adapter"},
            description = "Returns transformation's XSLT content (for OAI-PMH adapter process)",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Transformation retrieved",
                            content = @Content(schema = @Schema(implementation = TransformationResult.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid transformation ID format",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing authentication",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights (user is not the owner of the transformation or admin)",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GET
    @Path("transformations/{id}/xslt")
    @Produces(MediaType.APPLICATION_XML)
    public Response getTransformationXslt(@PathParam("id") String id) throws UnauthorizedException, InsufficientRightsException, cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException {
        //authorization: must be logged in
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        //fetch transformation
        XmlTransformation transformationFetched = getTransformationById(id);
        //check rights: only owner or admin can access
        if (!transformationFetched.getOwnerLogin().equals(user.getLogin()) && !user.isAdmin()) {
            throw new InsufficientRightsException("Only admin or owner can access the transformation");
        }
        //respond
        return Response.ok()
                .entity(transformationFetched.getXslt())
                .build();
    }

    @Operation(
            summary = "Upload XSLT for OAI-PMH adapter transformation",
            tags = {"Processes", "OAI-Adapter"},
            description = "Uploads XSLT content for the transformation for OAI-PMH adapter process",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "XSLT uploaded",
                            content = @Content(schema = @Schema(implementation = TransformationResult.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing authentication",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights (user is not the owner of the transformation)",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @PUT
    @Path("transformations/{id}/xslt")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response setTransformationXslt(@PathParam("id") String id,
                                          @RequestBody(content = @Content(schema = @Schema(type = "string", description = "Raw XSLT content (UTF-8 text)"))
                                          ) String xslt)
            throws UnauthorizedException, InsufficientRightsException, cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException, BadArgumentException {
        //authorization: must be logged in
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        //fetch transformation
        XmlTransformation transformationFetched = getTransformationById(id);
        //check rights: only owner
        if (!transformationFetched.getOwnerLogin().equals(user.getLogin())) {
            throw new InsufficientRightsException("Only owner can upload XSLT for the transformation");
        }
        if (xslt == null || xslt.isEmpty()) {
            throw new BadArgumentException("XSLT content cannot be empty");
        }
        // žádná validace/parsing – jen uložit raw text
        transformationFetched.setXslt(xslt);
        XmlTransformation updated = null;
        try {
            updated = xmlTransformationDao.updateTransformation(transformationFetched);
        } catch (UnknownRecordException e) {
            throw new cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException(e.getMessage());
        }
        //respond
        return Response.ok()
                .entity(toTransformationResult(updated))
                .build();
    }

    private XmlTransformation getTransformationById(String idStr) throws cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException, BadRequestException {
        try {
            Long id = Long.valueOf(idStr);
            return xmlTransformationDao.getTransformation(id);
        } catch (UnknownRecordException ex) {
            throw new cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException("Transformation with id " + idStr + " not found: " + ex.getMessage());
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid transformation ID format: " + idStr);
        }
    }

    private TransformationResult toTransformationResult(XmlTransformation transformation) {
        return new TransformationResult(
                transformation.getId(),
                transformation.getOwnerLogin(),
                transformation.getType().name(),
                transformation.getName(),
                transformation.getDescription(),
                FORMATTER.format(transformation.getCreated().toInstant())
        );
    }

    private XmlTransformationType parseTransformationType(JsonObject root, String paramName) throws BadArgumentException {
        if (!root.containsKey(paramName)) {
            throw new BadArgumentException("Missing mandatory parameter: " + paramName);
        }
        String typeStr = root.getString(paramName);
        try {
            return XmlTransformationType.valueOf(typeStr);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BadArgumentException("Invalid transformation type: " + typeStr);
        }
    }

    record TransformationCreate(@NotNull XmlTransformationType type,
                                @NotNull String name, String description) {
    }

    public record TransformationResult(@NotNull Long id, @NotNull String ownerLogin, @NotNull String type,
                                       @NotNull String name,
                                       String description,
                                       @NotNull String created) {
    }

}

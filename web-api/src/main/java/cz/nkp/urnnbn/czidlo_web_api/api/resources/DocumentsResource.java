package cz.nkp.urnnbn.czidlo_web_api.api.resources;

import cz.nkp.urnnbn.core.AccessRestriction;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.czidlo_web_api.api.ApiError;
import cz.nkp.urnnbn.czidlo_web_api.api.AuthenticatedUserPrincipal;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.DocumentManager;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.DocumentManagerImpl;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.InstanceManager;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.InstanceManagerImpl;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.*;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.Record;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.*;
import cz.nkp.urnnbn.services.exceptions.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.io.StringReader;

@Path("/documents")
public class DocumentsResource extends AbstractResource {

    @Context
    private SecurityContext securityContext;

    private static final DocumentManager documentManager = new DocumentManagerImpl();
    private static final InstanceManager instanceManager = new InstanceManagerImpl();

    @Operation(
            summary = "Create digital document",
            tags = "Documents",
            description = "Creates new digital document, either by provided URN:NBN or by letting the system assign new URN:NBN.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created",
                            content = @Content(schema = @Schema(implementation = Record.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data in request body",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights (if URN:NBN is provided, user must manage the registrar)",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Registrar or archiver not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createDigitalDocument(
            @RequestBody(
                    content = @Content(schema = @Schema(implementation = RecordToBeCreatedOrUpdated.class)),
                    description = "JSON object with digital document input data",
                    required = true
            ) String body) throws UnknownRecordException, UnauthorizedException, InsufficientRightsException, ConflictException, BadArgumentException {
        //authorization: must be admin or user with right to manage registrar (if URN:NBN is provided)
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
        RecordToBeCreatedOrUpdated recordToBeCreatedOrUpdated = RecordToBeCreatedOrUpdated.fromJsonObject(root);
        //register digital document
        try {
            UrnNbn urnNbn = documentManager.createRecord(recordToBeCreatedOrUpdated, user.getLogin());
            Record created = documentManager.getRecord(urnNbn);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (UnknownUserException e) {
            throw new RuntimeException(e);
        } catch (RegistrarScopeIdentifierCollisionException e) {
            throw new RuntimeException(e);
        } catch (UnknownArchiverException e) {
            throw new RuntimeException(e);
        } catch (IncorrectPredecessorStatus e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(
            summary = "Fetch document record by URN:NBN",
            tags = "Documents",
            description = "Returns digital document identified by the given URN:NBN.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Digital document",
                            content = @Content(schema = @Schema(implementation = Record.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid URN:NBN format",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Digital document not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GET
    @Path("{urn}")
    public Response getDocumentByUrnNbn(
            @Parameter(description = "URN:NBN identifier of the digital document", required = true)
            @PathParam("urn") String urn) throws UnknownRecordException {
        //authorization: none

        UrnNbn urnNbn;
        try {
            urnNbn = UrnNbn.valueOf(urn);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid URN:NBN format: " + e.getMessage());
        }
        Record record = documentManager.getRecord(urnNbn);
        if (record == null) {
            throw new UnknownRecordException("Digital document with URN:NBN " + urn + " not found");
        }
        return Response.ok(record).build();
    }

    @Operation(
            summary = "Update document",
            tags = "Documents",
            description = "Updates digital document identified by the given URN:NBN, including it's intelecutal entity and associated records.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Updated digital document",
                            content = @Content(schema = @Schema(implementation = Record.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid URN:NBN format or invalid input data",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights to manage this document's registrar",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Digital document not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Digital document not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @PUT
    @Path("{urn}")
    public Response updateDocumentByUrnNbn(
            @Parameter(description = "URN:NBN identifier of the digital document", required = true)
            @PathParam("urn") String urn,
            @RequestBody(
                    content = @Content(schema = @Schema(implementation = RecordToBeCreatedOrUpdated.class)),
                    description = "JSON object with digital document input data",
                    required = true
            ) String body) throws UnknownRecordException, UnauthorizedException, InsufficientRightsException, BadArgumentException {
        //authorization: must be admin or user with right to manage this registrar
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        //parse and validate urn
        UrnNbn urnNbn;
        try {
            urnNbn = UrnNbn.valueOf(urn);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid URN:NBN format: " + e.getMessage());
        }
        if (!user.isAdmin() && !principal.managesRegistrar(urnNbn.getRegistrarCode().toString())) {
            throw new InsufficientRightsException("Only admin or user with right to manage registrar " + urnNbn.getRegistrarCode().toString() + " can perform this operation");
        }
        //parse mandatory body to json
        if (body == null || body.isEmpty()) {
            return mandatoryBodyMissingResponse();
        }
        JsonObject root;
        try (JsonReader r = Json.createReader(new StringReader(body))) {
            root = r.readObject();
        }

        RecordToBeCreatedOrUpdated record = RecordToBeCreatedOrUpdated.fromJsonObject(root);
        //check URN:NBN in body (if present) against path parameter
        if (record.urnNbn != null && !urnNbn.toString().equals(record.urnNbn)) {
            throw new BadArgumentException("URN:NBN in path parameter and in request body do not match");
        }
        //update digital document
        try {
            documentManager.updateRecord(record, user.getLogin());
            Record updated = documentManager.getRecord(urnNbn);
            return Response.ok().entity(updated).build();
        } catch (UnknownUserException e) {
            throw new RuntimeException(e);
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Operation(
            summary = "Deactivate URN:NBN",
            tags = "Documents",
            description = "Deactivates given URN:NBN identifier and thus the associated digital document.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Deactivated, i.e. deactivation record created"),
                    @ApiResponse(responseCode = "400", description = "Invalid URN:NBN format",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights to deactivate URN:NBN",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Digital document not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "409", description = "URN:NBN is already deactivated",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @POST
    @Path("{urn}/deactivation")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response deactivateUrnNbn(
            @Parameter(description = "URN:NBN identifier of the digital document", required = true)
            @PathParam("urn") String urn, String note) throws UnknownRecordException, UnauthorizedException, InsufficientRightsException, ConflictException {
        //authorization: must be admin or user with right to manage this registrar
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        UrnNbn urnNbn;
        try {
            urnNbn = UrnNbn.valueOf(urn);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid URN:NBN format: " + e.getMessage());
        }
        if (!user.isAdmin() && !principal.managesRegistrar(urnNbn.getRegistrarCode().toString())) {
            throw new InsufficientRightsException("Only admin or user with right to manage registrar " + urnNbn.getRegistrarCode().toString() + " can perform this operation");
        }
        boolean deactivated = documentManager.deactivateRecord(urnNbn, note, user.getLogin());
        if (!deactivated) {
            throw new ConflictException("URN:NBN " + urn + " is already deactivated");
        }
        Record record = documentManager.getRecord(urnNbn);
        return Response.ok(record).build();
    }

    @Operation(
            summary = "Reactivate URN:NBN",
            tags = "Documents",
            description = "Reactivates deactivated URN:NBN identifier and thus the associated digital document.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reactivated, i.e. deactivation record removed"),
                    @ApiResponse(responseCode = "400", description = "Invalid URN:NBN format",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights to reactivate URN:NBN (must be admin)",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Digital document not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "409", description = "URN:NBN is not deactivated",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @DELETE
    @Path("{urn}/deactivation")
    public Response reactivateUrnNbn(
            @Parameter(description = "URN:NBN identifier of the digital document", required = true)
            @PathParam("urn") String urn) throws UnknownRecordException, UnauthorizedException, InsufficientRightsException, ConflictException {
        //authorization: must be admin
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin()) {
            throw new InsufficientRightsException("Only admin can perform this operation");
        }

        UrnNbn urnNbn;
        try {
            urnNbn = UrnNbn.valueOf(urn);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid URN:NBN format: " + e.getMessage());
        }

        boolean reactivated = documentManager.reactivateRecord(urnNbn, user.getLogin());
        if (!reactivated) {
            throw new ConflictException("URN:NBN " + urn + " still active, cannot reactivate");
        }
        Record record = documentManager.getRecord(urnNbn);
        return Response.ok(record).build();
    }

    @Operation(
            summary = "Create digital instance",
            tags = "Documents",
            description = "Creates new digital instance linked to document identified by the given URN:NBN.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created",
                            content = @Content(schema = @Schema(implementation = DigInst.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid URN:NBN format or invalid digital instance data in request body",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights to add digital instance to this document",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Digital document not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @POST
    @Path("{urn}/instances")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createDigitalInstance(
            @Parameter(description = "URN:NBN identifier of the digital document", required = true)
            @PathParam("urn") String urn, @RequestBody(
                    content = @Content(schema = @Schema(implementation = InstanceCreate.class)),
                    description = "JSON object representing archiver parameters",
                    required = true
            ) String body) throws UnknownRecordException, UnauthorizedException, InsufficientRightsException, ConflictException, BadArgumentException {
        //authorization: must be admin or user with right to manage registrar of the digital library hosting new digital instance
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        //parse and validate urn
        UrnNbn urnNbn;
        try {
            urnNbn = UrnNbn.valueOf(urn);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid URN:NBN format: " + e.getMessage());
        }
        //parse mandatory body to json
        if (body == null || body.isEmpty()) {
            return mandatoryBodyMissingResponse();
        }
        JsonObject root;
        try (JsonReader r = Json.createReader(new StringReader(body))) {
            root = r.readObject();
        }
        //extract and validate parameters
        if (!root.containsKey("libraryId")) {
            throw new BadArgumentException("Missing mandatory parameter: libraryId");
        }
        Long libraryId = readParam("libraryId", name -> root.getJsonNumber(name).longValue());
        String url = readParam("url", root::getString);
        checkDigitalInstanceUrl(url);
        String format = null;
        if (root.containsKey("format")) {
            format = readParam("format", root::getString);
        }
        String accessibility = null;
        if (root.containsKey("accessibility")) {
            accessibility = readParam("accessibility", root::getString);
        }
        AccessRestriction accessRestriction = null;
        if (root.containsKey("accessRestriction")) {
            String accessRestrictionStr = readParam("accessRestriction", root::getString);
            accessRestriction = parseAccessRestriction(accessRestrictionStr);
        }
        DigInst created = instanceManager.createDigitalInstance(user.getLogin(), urnNbn, libraryId, url, format, accessibility, accessRestriction);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    private AccessRestriction parseAccessRestriction(String accessRestriction) throws BadArgumentException {
        if (accessRestriction == null || accessRestriction.isEmpty()) {
            return null;
        }
        try {
            return AccessRestriction.valueOf(accessRestriction.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadArgumentException("Invalid accessRestriction: " + accessRestriction + ". Allowed values are: UNKNOWN, UNLIMITED_ACCESS, LIMITED_ACCESS");
        }
    }

    private void checkDigitalInstanceUrl(String url) throws BadArgumentException {
        //must not be null or empty
        if (url == null || url.isEmpty()) {
            throw new BadArgumentException("Invalid url: " + url + ". Must not be null or empty");
        }
        //must start with "http://" or "https://"
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new BadArgumentException("Invalid url: " + url + ". Must start with http:// or https://");
        }
        int minLength = 11; //http://a.cz
        if (url.length() < minLength) {
            throw new BadArgumentException("Invalid url: " + url + ". Min length is " + minLength + " characters");
        }
        int maxLength = 200;
        if (url.length() > maxLength) {
            throw new BadArgumentException("Invalid url: " + url + ". Max length is " + maxLength + " characters");
        }
    }


    record InstanceCreate(@NotNull String url, @NotNull Long libraryId, String format, String accessibility,
                          String accessRestriction) {
    }

}

package cz.nkp.urnnbn.czidlo_web_api.api.resources;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.czidlo_web_api.api.ApiError;
import cz.nkp.urnnbn.czidlo_web_api.api.AuthenticatedUserPrincipal;
import cz.nkp.urnnbn.czidlo_web_api.api.archivers.archiver_manager.ArchiverManager;
import cz.nkp.urnnbn.czidlo_web_api.api.archivers.archiver_manager.ArchiverManagerImpl;
import cz.nkp.urnnbn.czidlo_web_api.api.archivers.core.Archiver;
import cz.nkp.urnnbn.czidlo_web_api.api.archivers.core.ArchiverList;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.json.*;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.io.StringReader;
import java.util.List;
import java.util.function.Function;

@Path("/archivers")
public class ArchiversResource extends AbstractResource {
    //private static final ArchiverManager archiverManager = new ArchiverManagerMockInMemory();
    private static final ArchiverManager archiverManager = new ArchiverManagerImpl();

    @Context
    private SecurityContext securityContext;

    @Operation(
            summary = "Create archiver",
            tags = "Archivers",
            description = "Creates a new archiver.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "The archiver",
                            content = @Content(schema = @Schema(implementation = Archiver.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid archiver params",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createArchiver(
            @RequestBody(
                    content = @Content(schema = @Schema(implementation = ArchiverCreate.class)),
                    description = "JSON object representing archiver parameters",
                    required = true
            ) String body) throws DuplicateRecordException, UnauthorizedException, InsufficientRightsException, BadArgumentException {
        //authorization: must be admin
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin()) {
            throw new InsufficientRightsException("Only admin can manage archivers");
        }

        if (body == null || body.isEmpty()) {
            throw new BadRequestException("Missing mandatory body");
        }

        JsonObject root;
        try (JsonReader r = Json.createReader(new StringReader(body))) {
            root = r.readObject();
        }

        String name = readParam("name", root::getString);
        checkArchiverName(name);
        String desc = null;
        if (root.containsKey("description")) {
            desc = readParam("description", root::getString);
            checkArchiverDescription(desc);
        }

        Archiver a = archiverManager.createArchiver(user.getLogin(), name, desc);
        return Response.ok(a).build();
    }

    @Operation(
            summary = "Get archiver",
            tags = "Archivers",
            description = "Returns an archiver by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The archiver",
                            content = @Content(schema = @Schema(implementation = Archiver.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Archiver not found or ID is not integer",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GET
    @Path("{id}")
    public Response getArchiverById(
            @Parameter(description = "ID of the archiver", required = true) @PathParam("id") long id) throws UnknownRecordException {
        //authorization: none

        Archiver a = archiverManager.getArchiver(id);
        return Response.ok(a).build();
    }

    @Operation(
            summary = "Get all archivers",
            tags = "Archivers",
            description = "Returns a list of all archivers.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of archivers",
                            content = @Content(schema = @Schema(implementation = ArchiverList.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GET
    public Response getArchivers() {
        //authorization: none

        List<Archiver> a = archiverManager.getArchivers();
        return Response.ok(new ArchiverList(a)).build();
    }

    @Operation(
            summary = "Update archiver",
            tags = "Archivers",
            description = "Updates an archiver by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Success",
                            content = @Content(schema = @Schema(implementation = Archiver.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid ID supplied or invalid archiver params",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Archiver not found or ID is not integer",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateArchiver(
            @Parameter(description = "ID of the archiver", required = true) @PathParam("id") long id,
            @RequestBody(
                    content = @Content(schema = @Schema(implementation = ArchiverUpdate.class)),
                    description = "JSON object representing archiver parameters",
                    required = true
            ) String body) throws UnknownRecordException, UnauthorizedException, InsufficientRightsException, BadArgumentException {
        //authorization: must be admin
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin()) {
            throw new InsufficientRightsException("Only admin can manage archivers");
        }

        if (body == null || body.isEmpty()) {
            throw new BadRequestException("Missing mandatory body");
        }

        JsonObject root;
        try (JsonReader r = Json.createReader(new StringReader(body))) {
            root = r.readObject();
        }

        String name = readParam("name", root::getString);
        checkArchiverName(name);
        String desc = null;
        if (root.containsKey("description")) {
            desc = readParam("description", root::getString);
            checkArchiverDescription(desc);
        }
        boolean hidden = readParam("hidden", root::getBoolean);

        Archiver a = archiverManager.updateArchiver(user.getLogin(), id, name, desc, hidden);
        return Response.ok(a).build();

    }

    @Operation(
            summary = "Delete archiver",
            tags = "Archivers",
            description = "Deletes an archiver by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "204", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Archiver not found or ID is not integer",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @DELETE
    @Path("/{id}")
    public Response deleteArchiver(@PathParam("id") long id) throws UnknownRecordException, UnauthorizedException, InsufficientRightsException {
        //authorization: must be admin
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin()) {
            throw new InsufficientRightsException("Only admin can manage archivers");
        }

        archiverManager.deleteArchiver(user.getLogin(), id);
        return Response.noContent().build();
    }

    record ArchiverCreate(@NotNull String name, String description) {
    }

    record ArchiverUpdate(@NotNull String name, String description, @NotNull boolean hidden) {
    }

    private void checkArchiverName(String name) throws BadArgumentException {
        //must not be null or empty
        if (name == null || name.isEmpty()) {
            throw new BadArgumentException("Invalid name: " + name + ". Must not be null or empty");
        }
        //max length = 100
        if (name.length() > 100) {
            throw new BadArgumentException("Invalid name: " + name + ". Max length is 50 characters");
        }
    }

    private void checkArchiverDescription(String desc) throws BadArgumentException {
        //max length = 200
        if (desc != null && desc.length() > 200) {
            throw new BadArgumentException("Invalid description: " + desc + ". Max length is 200 characters");
        }
    }

}

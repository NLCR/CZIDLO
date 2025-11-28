package cz.nkp.urnnbn.czidlo_web_api.api;

import cz.nkp.urnnbn.czidlo_web_api.api.archivers.archiver_manager.ArchiverManager;
import cz.nkp.urnnbn.czidlo_web_api.api.archivers.archiver_manager.ArchiverManagerImpl;
import cz.nkp.urnnbn.czidlo_web_api.api.archivers.archiver_manager.ArchiverManagerMockInMemory;
import cz.nkp.urnnbn.czidlo_web_api.api.archivers.core.Archiver;
import cz.nkp.urnnbn.czidlo_web_api.api.archivers.core.ArchiverList;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.DuplicateRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.json.*;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.StringReader;
import java.util.List;
import java.util.function.Function;

@Path("/archivers")
public class ArchiversResource extends AbstractResource {
    //private static final ArchiverManager archiverManager = new ArchiverManagerMockInMemory();
    private static final ArchiverManager archiverManager = new ArchiverManagerImpl();

    //TODO: in production replace with real user from authentication
    private static final String DEFAULT_USER = "superAdmin";

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
            ) String body) throws DuplicateRecordException {
        String user = DEFAULT_USER; //TODO: must be admin or have right to manage this registrar

        if (body == null || body.isEmpty()) {
            throw new BadRequestException("Missing mandatory body");
        }

        JsonObject root;
        try (JsonReader r = Json.createReader(new StringReader(body))) {
            root = r.readObject();
        }

        String name = readParam("name", root::getString);
        String desc = null;
        if (root.containsKey("description")) {
            desc = readParam("description", root::getString);
        }

        Archiver a = archiverManager.createArchiver(user, name, desc);
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
                    @ApiResponse(responseCode = "404", description = "Archiver not found or ID is not integer"),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GET
    @Path("{id}")
    public Response getArchiversById(
            @Parameter(description = "ID of the archiver", required = true) @PathParam("id") long id) throws UnknownRecordException {
        String user = DEFAULT_USER;

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
        String user = DEFAULT_USER;

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
                    @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Archiver not found or ID is not integer"),
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
            ) String body) throws UnknownRecordException, DuplicateRecordException {
        String user = DEFAULT_USER; //TODO: must be admin or have right to manage this registrar

        if (body == null || body.isEmpty()) {
            throw new BadRequestException("Missing mandatory body");
        }

        JsonObject root;
        try (JsonReader r = Json.createReader(new StringReader(body))) {
            root = r.readObject();
        }

        String name = readParam("name", root::getString);
        String desc = null;
        if (root.containsKey("description")) {
            desc = readParam("description", root::getString);
        }
        boolean hidden = readParam("hidden", root::getBoolean);

        Archiver a = archiverManager.updateArchiver(user, id, name, desc, hidden);
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
                    @ApiResponse(responseCode = "404", description = "Archiver not found or ID is not integer"),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @DELETE
    @Path("/{id}")
    public Response deleteArchiver(@PathParam("id") long id) throws UnknownRecordException {
        String user = DEFAULT_USER; //TODO: must be admin or have right to manage this registrar

        archiverManager.deleteArchiver(user, id);
        return Response.noContent().build();
    }

    private <T> T readParam(String paramName, Function<String, T> funk) {
        try {
            return funk.apply(paramName);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Missing mandatory parameter: " + paramName);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid type for parameter: " + paramName);
        }
    }

    record ArchiverCreate(@NotNull String name, String description) {
    }

    record ArchiverUpdate(@NotNull String name, String description, @NotNull boolean hidden) {
    }

}

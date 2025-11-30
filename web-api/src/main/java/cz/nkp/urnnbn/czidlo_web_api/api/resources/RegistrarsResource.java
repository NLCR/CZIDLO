package cz.nkp.urnnbn.czidlo_web_api.api.resources;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.czidlo_web_api.api.ApiError;
import cz.nkp.urnnbn.czidlo_web_api.api.AuthenticatedUserPrincipal;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.*;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Catalogue;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.DigitalLibrary;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Registrar;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.RegistrarList;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.registrar_manager.RegistrarManager;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.registrar_manager.RegistrarManagerImpl;
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
import java.util.List;
import java.util.function.Function;

@Path("/registrars")
public class RegistrarsResource extends AbstractResource {

    @Context
    private SecurityContext securityContext;

    //private static final RegistrarManager registrarManager = new RegistrarManagerMockInMemory();
    private static final RegistrarManager registrarManager = new RegistrarManagerImpl();

    @Operation(
            summary = "Create registrar",
            tags = "Registrars",
            description = "Creates a new registrar.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "The registrar",
                            content = @Content(schema = @Schema(implementation = Registrar.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid registrar params",
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
    public Response createRegistrar(
            @RequestBody(
                    content = @Content(schema = @Schema(implementation = RegistrarCreate.class)),
                    description = "JSON object representing registrar parameters",
                    required = true
            ) String body) throws DuplicateRecordException, BadArgumentException, InsufficientRightsException, UnauthorizedException {

        //authorization: must be admin
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin()) {
            throw new InsufficientRightsException("Only admin can create registrars");
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
        String code = readParam("code", root::getString);
        checkRegistrarCode(code);
        String name = readParam("name", root::getString);
        checkRegistrarName(name);
        String desc = null;
        if (root.containsKey("description")) {
            desc = readParam("description", root::getString);
        }
        checkRegistrarDescription(desc);
        Boolean allowedRegistrationModeByResolver = readParam("allowedRegistrationModeByResolver", root::getBoolean);
        Boolean allowedRegistrationModeByReservation = readParam("allowedRegistrationModeByReservation", root::getBoolean);
        Boolean allowedRegistrationModeByRegistrar = readParam("allowedRegistrationModeByRegistrar", root::getBoolean);

        Registrar a = registrarManager.createRegistrar(user.getLogin(), code, name, desc, allowedRegistrationModeByResolver, allowedRegistrationModeByReservation, allowedRegistrationModeByRegistrar);
        return Response.ok(a).build();
    }

    @Operation(
            summary = "Get registrar",
            tags = "Registrars",
            description = "Returns a registrar by its code.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The registrar",
                            content = @Content(schema = @Schema(implementation = Registrar.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid registrar code",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Registrar not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GET
    @Path("{code}")
    public Response getRegistrarByCode(
            @Parameter(description = "Code of the registrar", required = true) @PathParam("code") String code) throws UnknownRecordException, BadArgumentException {
        //authorization: none

        Registrar a = registrarManager.getRegistrarByCode(code);
        return Response.ok(a).build();
    }

    @Operation(
            summary = "Get all registrars",
            tags = "Registrars",
            description = "Returns a list of all registrars.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of registrars",
                            content = @Content(schema = @Schema(implementation = RegistrarList.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GET
    public Response getRegistrars() {
        //authorization: none

        List<Registrar> a = registrarManager.getRegistrars();
        return Response.ok(new RegistrarList(a)).build();
    }

    @Operation(
            summary = "Update registrar",
            tags = "Registrars",
            description = "Updates a registrar by its code.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Success",
                            content = @Content(schema = @Schema(implementation = Registrar.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid registrar code or params",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Registrar not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @PUT
    @Path("/{code}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRegistrar(
            @Parameter(description = "Code of the registrar", required = true) @PathParam("code") String code,
            @RequestBody(
                    content = @Content(schema = @Schema(implementation = RegistrarUpdate.class)),
                    description = "JSON object representing registrar parameters",
                    required = true
            ) String body) throws UnknownRecordException, BadArgumentException, InsufficientRightsException, UnauthorizedException {
        //authorization: must be admin or user with right to manage this registrar
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin() && !principal.managesRegistrar(code)) {
            throw new InsufficientRightsException("Only admin or user with right to manage registrar " + code + " can perform this operation");
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
        String name = readParam("name", root::getString);
        checkRegistrarName(name);
        String desc = null;
        if (root.containsKey("description")) {
            desc = readParam("description", root::getString);
        }
        checkRegistrarDescription(desc);
        Boolean allowedRegistrationModeByResolver = readParam("allowedRegistrationModeByResolver", root::getBoolean);
        Boolean allowedRegistrationModeByReservation = readParam("allowedRegistrationModeByReservation", root::getBoolean);
        Boolean allowedRegistrationModeByRegistrar = readParam("allowedRegistrationModeByRegistrar", root::getBoolean);
        boolean hidden = readParam("hidden", root::getBoolean);

        Registrar a = registrarManager.updateRegistrar(user.getLogin(), code, name, desc, allowedRegistrationModeByResolver, allowedRegistrationModeByReservation, allowedRegistrationModeByRegistrar, hidden);
        return Response.ok(a).build();
    }

    @Operation(
            summary = "Delete registrar",
            tags = "Registrars",
            description = "Deletes a registrar by its code.",
            responses = {
                    @ApiResponse(
                            responseCode = "204", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Invalid registrar code",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Registrar not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "409", description = "Registrar cannot be deleted because it registers some documents",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @DELETE
    @Path("/{code}")
    public Response deleteRegistrar(@PathParam("code") String code) throws UnknownRecordException, BadArgumentException, ConflictException, UnauthorizedException, InsufficientRightsException {
        //authorization: must be admin or user with right to manage this registrar
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin() && !principal.managesRegistrar(code)) {
            throw new InsufficientRightsException("Only admin or user with right to manage registrar " + code + " can perform this operation");
        }

        registrarManager.deleteRegistrar(user.getLogin(), code);
        return Response.noContent().build();
    }

    @Operation(
            summary = "Create digital library",
            tags = "Registrars",
            description = "Creates a new digital library.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "The library",
                            content = @Content(schema = @Schema(implementation = DigitalLibraryCreate.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid registrar code or library params",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Registrar not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @POST
    @Path("/{code}/digital_libraries")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createDigitalLibrary(
            @Parameter(description = "Code of the registrar", required = true) @PathParam("code") String code,
            @RequestBody(
                    content = @Content(schema = @Schema(implementation = DigitalLibraryCreate.class)),
                    description = "JSON object representing digital library parameters",
                    required = true
            ) String body) throws UnknownRecordException, BadArgumentException, UnauthorizedException, InsufficientRightsException {
        //authorization: must be admin or user with right to manage this registrar
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin() && !principal.managesRegistrar(code)) {
            throw new InsufficientRightsException("Only admin or user with right to manage registrar " + code + " can perform this operation");
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
        String name = readParam("name", root::getString);
        checkDigitalLibraryName(name);
        String desc = null;
        if (root.containsKey("description")) {
            desc = readParam("description", root::getString);
        }
        checkDigitalLibraryDescription(desc);
        String url = readParam("url", root::getString);
        checkDigitalLibraryUrl(url);

        DigitalLibrary a = registrarManager.createLibrary(user.getLogin(), code, name, desc, url);
        return Response.ok(a).build();
    }

    @Operation(
            summary = "Update digital library",
            tags = "Registrars",
            description = "Updates a digital library by its id.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Success",
                            content = @Content(schema = @Schema(implementation = DigitalLibraryUpdate.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid registrar code or library params",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Registrar or digital library not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @PUT
    @Path("/{code}/digital_libraries/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateDigitalLibrary(
            @Parameter(description = "Code of the registrar", required = true) @PathParam("code") String code,
            @Parameter(description = "Id of the digital library", required = true) @PathParam("id") long id,
            @RequestBody(
                    content = @Content(schema = @Schema(implementation = DigitalLibraryUpdate.class)),
                    description = "JSON object representing digital library parameters",
                    required = true
            ) String body) throws UnknownRecordException, BadArgumentException, UnauthorizedException, InsufficientRightsException {
        //authorization: must be admin or user with right to manage this registrar
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin() && !principal.managesRegistrar(code)) {
            throw new InsufficientRightsException("Only admin or user with right to manage registrar " + code + " can perform this operation");
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
        String name = readParam("name", root::getString);
        checkDigitalLibraryName(name);
        String desc = null;
        if (root.containsKey("description")) {
            desc = readParam("description", root::getString);
        }
        checkDigitalLibraryDescription(desc);
        String url = readParam("url", root::getString);
        checkDigitalLibraryUrl(url);

        DigitalLibrary a = registrarManager.updateLibrary(user.getLogin(), code, id, name, desc, url);
        return Response.ok(a).build();
    }

    @Operation(
            summary = "Delete digital library",
            tags = "Registrars",
            description = "Deletes a digital library by its id.",
            responses = {
                    @ApiResponse(
                            responseCode = "204", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Invalid registrar code or digital library id",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Registrar or digital library not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @DELETE
    @Path("/{code}/digital_libraries/{id}")
    public Response deleteDigitalLibrary(
            @Parameter(description = "Code of the registrar", required = true) @PathParam("code") String code,
            @Parameter(description = "Id of the digital library", required = true) @PathParam("id") long id)
            throws UnknownRecordException, BadArgumentException, UnauthorizedException, InsufficientRightsException {
        //authorization: must be admin or user with right to manage this registrar
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin() && !principal.managesRegistrar(code)) {
            throw new InsufficientRightsException("Only admin or user with right to manage registrar " + code + " can perform this operation");
        }

        registrarManager.deleteLibrary(user.getLogin(), code, id);
        return Response.noContent().build();
    }

    @Operation(
            summary = "Create catalogue",
            tags = "Registrars",
            description = "Creates a new catalogue.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Catalogue created",
                            content = @Content(schema = @Schema(implementation = CatalogueCreate.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid registrar code or catalogue params",
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
    @Path("/{code}/catalogues")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCatalogue(
            @Parameter(description = "Code of the registrar", required = true) @PathParam("code") String code,
            @RequestBody(
                    content = @Content(schema = @Schema(implementation = CatalogueCreate.class)),
                    description = "JSON object representing catalogue parameters",
                    required = true
            ) String body) throws UnknownRecordException, BadArgumentException, UnauthorizedException, InsufficientRightsException {
        //authorization: must be admin or user with right to manage this registrar
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin() && !principal.managesRegistrar(code)) {
            throw new InsufficientRightsException("Only admin or user with right to manage registrar " + code + " can perform this operation");
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
        String name = readParam("name", root::getString);
        checkCatalogueName(name);
        String desc = null;
        if (root.containsKey("description")) {
            desc = readParam("description", root::getString);
        }
        checkCatalogueDescription(desc);
        String urlPrefix = readParam("urlPrefix", root::getString);
        checkCatalogueUrlPrefix(urlPrefix);

        Catalogue a = registrarManager.createCatalogue(user.getLogin(), code, name, desc, urlPrefix);
        return Response.ok(a).build();
    }

    @Operation(
            summary = "Update catalogue",
            tags = "Registrars",
            description = "Updates a catalogue by its id.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Success",
                            content = @Content(schema = @Schema(implementation = CatalogueUpdate.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid registrar code or catalogue params",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Registrar not found or catalogue not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @PUT
    @Path("/{code}/catalogue/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCatalogue(
            @Parameter(description = "Code of the registrar", required = true) @PathParam("code") String code,
            @Parameter(description = "Id of the catalogue", required = true) @PathParam("id") long id,
            @RequestBody(
                    content = @Content(schema = @Schema(implementation = CatalogueUpdate.class)),
                    description = "JSON object representing catalogue parameters",
                    required = true
            ) String body) throws UnknownRecordException, BadArgumentException, UnauthorizedException, InsufficientRightsException {
        //authorization: must be admin or user with right to manage this registrar
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin() && !principal.managesRegistrar(code)) {
            throw new InsufficientRightsException("Only admin or user with right to manage registrar " + code + " can perform this operation");
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
        String name = readParam("name", root::getString);
        checkCatalogueName(name);
        String desc = null;
        if (root.containsKey("description")) {
            desc = readParam("description", root::getString);
        }
        checkCatalogueDescription(desc);
        String urlPrefix = readParam("urlPrefix", root::getString);
        checkCatalogueUrlPrefix(urlPrefix);

        Catalogue a = registrarManager.updateCatalogue(user.getLogin(), code, id, name, desc, urlPrefix);
        return Response.ok(a).build();
    }

    @Operation(
            summary = "Delete catalogue",
            tags = "Registrars",
            description = "Deletes a catalogue by its id.",
            responses = {
                    @ApiResponse(
                            responseCode = "204", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Invalid registrar code or catalogue id",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Registrar not found or catalogue not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @DELETE
    @Path("/{code}/catalogue/{id}")
    public Response deleteCatalogue(
            @Parameter(description = "Code of the registrar", required = true) @PathParam("code") String code,
            @Parameter(description = "Id of the catalogue", required = true) @PathParam("id") long id)
            throws UnknownRecordException, BadArgumentException, UnauthorizedException, InsufficientRightsException {
        //authorization: must be admin or user with right to manage this registrar
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin() && !principal.managesRegistrar(code)) {
            throw new InsufficientRightsException("Only admin or user with right to manage registrar " + code + " can perform this operation");
        }

        registrarManager.deleteCatalogue(user.getLogin(), code, id);
        return Response.noContent().build();
    }

    record RegistrarCreate(@NotNull String code, @NotNull String name, String description,
                           @NotNull boolean allowedRegistrationModeByResolver,
                           @NotNull boolean allowedRegistrationModeByReservation,
                           @NotNull boolean allowedRegistrationModeByRegistrar) {
    }

    record RegistrarUpdate(@NotNull String name, String description, @NotNull boolean allowedRegistrationModeByResolver,
                           @NotNull boolean allowedRegistrationModeByReservation,
                           @NotNull boolean allowedRegistrationModeByRegistrar, @NotNull boolean hidden) {
    }

    record DigitalLibraryCreate(@NotNull String name, String description, @NotNull String url) {
    }

    record DigitalLibraryUpdate(@NotNull String name, String description, @NotNull String url) {
    }

    record CatalogueCreate(@NotNull String name, String description, @NotNull String urlPrefix) {
    }

    record CatalogueUpdate(@NotNull String name, String description, @NotNull String urlPrefix) {
    }

    private void checkRegistrarCode(String code) throws BadArgumentException {
        //must not be null or empty
        if (code == null || code.isEmpty()) {
            throw new BadArgumentException("Invalid code: " + code + ". Must not be null or empty");
        }
        //must be alphanumeric
        if (!code.matches("^[a-zA-Z0-9]+$")) {
            throw new BadArgumentException("Invalid code: " + code + ". Must be alphanumeric (a-z, A-Z, 0-9)");
        }
        //length <2;6> characters
        if (code.length() < 2 || code.length() > 6) {
            throw new BadArgumentException("Invalid code: " + code + ". Length must be between 2 and 6 characters");
        }
    }

    private void checkRegistrarName(String name) throws BadArgumentException {
        //must not be null or empty
        if (name == null || name.isEmpty()) {
            throw new BadArgumentException("Invalid name: " + name + ". Must not be null or empty");
        }
        //max length = 50
        if (name.length() > 50) {
            throw new BadArgumentException("Invalid name: " + name + ". Max length is 50 characters");
        }
    }

    private void checkRegistrarDescription(String desc) throws BadArgumentException {
        //max length = 200
        if (desc != null && desc.length() > 200) {
            throw new BadArgumentException("Invalid description: " + desc + ". Max length is 200 characters");
        }
    }

    private void checkCatalogueName(String name) throws BadArgumentException {
        //must not be null or empty
        if (name == null || name.isEmpty()) {
            throw new BadArgumentException("Invalid name: " + name + ". Must not be null or empty");
        }
        //max length = 30
        if (name.length() > 30) {
            throw new BadArgumentException("Invalid name: " + name + ". Max length is 30 characters");
        }
    }

    private void checkCatalogueDescription(String desc) throws BadArgumentException {
        //max length = 200
        if (desc != null && desc.length() > 200) {
            throw new BadArgumentException("Invalid description: " + desc + ". Max length is 200 characters");
        }
    }

    private void checkCatalogueUrlPrefix(String urlPrefix) throws BadArgumentException {
        //must not be null or empty
        if (urlPrefix == null || urlPrefix.isEmpty()) {
            throw new BadArgumentException("Invalid urlPrefix: " + urlPrefix + ". Must not be null or empty");
        }
        //must start with "http://" or "https://"
        if (!urlPrefix.startsWith("http://") && !urlPrefix.startsWith("https://")) {
            throw new BadArgumentException("Invalid urlPrefix: " + urlPrefix + ". Must start with http:// or https://");
        }
        int minLength = 11; //http://a.cz
        if (urlPrefix.length() < minLength) {
            throw new BadArgumentException("Invalid url: " + urlPrefix + ". Min length is " + minLength + " characters");
        }
        int maxLength = 100;
        if (urlPrefix.length() > maxLength) {
            throw new BadArgumentException("Invalid url: " + urlPrefix + ". Max length is " + maxLength + " characters");
        }
    }

    private void checkDigitalLibraryName(String name) throws BadArgumentException {
        //must not be null or empty
        if (name == null || name.isEmpty()) {
            throw new BadArgumentException("Invalid name: " + name + ". Must not be null or empty");
        }
        //max length = 30
        if (name.length() > 30) {
            throw new BadArgumentException("Invalid name: " + name + ". Max length is 30 characters");
        }
    }

    private void checkDigitalLibraryDescription(String desc) throws BadArgumentException {
        //max length = 200
        if (desc != null && desc.length() > 200) {
            throw new BadArgumentException("Invalid description: " + desc + ". Max length is 200 characters");
        }
    }

    private void checkDigitalLibraryUrl(String url) throws BadArgumentException {
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
        int maxLength = 100;
        if (url.length() > maxLength) {
            throw new BadArgumentException("Invalid url: " + url + ". Max length is " + maxLength + " characters");
        }
    }

}

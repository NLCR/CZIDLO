package cz.nkp.urnnbn.czidlo_web_api.api;

import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.BadArgumentException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.ConflictException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.DuplicateRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Catalogue;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.DigitalLibrary;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.Registrar;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.core.RegistrarList;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.registrar_manager.RegistrarManager;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.registrar_manager.RegistrarManagerImpl;
import cz.nkp.urnnbn.czidlo_web_api.api.registrars.registrar_manager.RegistrarManagerMockInMemory;
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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.StringReader;
import java.util.List;
import java.util.function.Function;

@Path("/registrars")
public class RegistrarResources extends AbstractResource {
    //private static final RegistrarManager registrarManager = new RegistrarManagerMockInMemory();
    private static final RegistrarManager registrarManager = new RegistrarManagerImpl();

    //TODO: in production replace with real user from authentication
    private static final String DEFAULT_USER = "superAdmin";

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
            ) String body) throws DuplicateRecordException, BadArgumentException {
        String user = DEFAULT_USER; //TODO: must be admin

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

        Registrar a = registrarManager.createRegistrar(user, code, name, desc, allowedRegistrationModeByResolver, allowedRegistrationModeByReservation, allowedRegistrationModeByRegistrar);
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
        String user = DEFAULT_USER;

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
        String user = DEFAULT_USER;

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
            ) String body) throws UnknownRecordException, DuplicateRecordException, BadArgumentException {
        String user = DEFAULT_USER; //TODO: must be admin or have right to manage this registrar

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

        Registrar a = registrarManager.updateRegistrar(user, code, name, desc, allowedRegistrationModeByResolver, allowedRegistrationModeByReservation, allowedRegistrationModeByRegistrar, hidden);
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
    public Response deleteRegistrar(@PathParam("code") String code) throws UnknownRecordException, BadArgumentException, ConflictException {
        String user = DEFAULT_USER; //TODO: must be admin or have right to manage this registrar

        registrarManager.deleteRegistrar(user, code);
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
            ) String body) throws UnknownRecordException, BadArgumentException {
        String user = DEFAULT_USER; //TODO: must be admin or have right to manage this registrar

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

        DigitalLibrary a = registrarManager.createLibrary(user, code, name, desc, url);
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
            ) String body) throws UnknownRecordException, BadArgumentException {
        String user = DEFAULT_USER; //TODO: must be admin or have right to manage this registrar

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

        DigitalLibrary a = registrarManager.updateLibrary(user, code, id, name, desc, url);
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
            throws UnknownRecordException, BadArgumentException {
        String user = DEFAULT_USER; //TODO: must be admin or have right to manage this registrar

        registrarManager.deleteLibrary(user, code, id);
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
            ) String body) throws UnknownRecordException, BadArgumentException {
        String user = DEFAULT_USER; //TODO: must be admin or have right to manage this registrar

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

        Catalogue a = registrarManager.createCatalogue(user, code, name, desc, urlPrefix);
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
                    @ApiResponse(responseCode = "404", description = "Registrar not found or code is not string, catalogue not found or id is not integer"),
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
            ) String body) throws UnknownRecordException, BadArgumentException {
        String user = DEFAULT_USER; //TODO: must be admin or have right to manage this registrar

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

        Catalogue a = registrarManager.updateCatalogue(user, code, id, name, desc, urlPrefix);
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
                    @ApiResponse(responseCode = "404", description = "Registrar not found or code is not string, catalogue not found or id is not integer"),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @DELETE
    @Path("/{code}/catalogue/{id}")
    public Response deleteCatalogue(
            @Parameter(description = "Code of the registrar", required = true) @PathParam("code") String code,
            @Parameter(description = "Id of the catalogue", required = true) @PathParam("id") long id)
            throws UnknownRecordException, BadArgumentException {
        String user = DEFAULT_USER; //TODO: must be admin or have right to manage this registrar

        registrarManager.deleteCatalogue(user, code, id);
        return Response.noContent().build();

    }

    private Response mandatoryBodyMissingResponse() {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ApiError("Missing mandatory body"))
                .build();
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
        //max length = 100
        if (urlPrefix.length() > 100) {
            throw new BadArgumentException("Invalid urlPrefix: " + urlPrefix + ". Max length is 100 characters");
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
        //max length = 100
        if (url.length() > 100) {
            throw new BadArgumentException("Invalid url: " + url + ". Max length is 100 characters");
        }
    }

}

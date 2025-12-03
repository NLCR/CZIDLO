package cz.nkp.urnnbn.czidlo_web_api.api.resources;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.czidlo_web_api.api.ApiError;
import cz.nkp.urnnbn.czidlo_web_api.api.AuthenticatedUserPrincipal;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.*;
import cz.nkp.urnnbn.czidlo_web_api.api.users.core.UserDetails;
import cz.nkp.urnnbn.czidlo_web_api.api.users.core.UserList;
import cz.nkp.urnnbn.czidlo_web_api.api.users.user_manager.UserManager;
import cz.nkp.urnnbn.czidlo_web_api.api.users.user_manager.UserManagerImpl;
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

@Path("/users")
public class UsersResource extends AbstractResource {

    @Context
    private SecurityContext securityContext;

    //private static final UserManager userManager = new UserManagerMockInMemory();
    private static final UserManager userManager = new UserManagerImpl();

    @Operation(
            summary = "Create new user",
            tags = "Users",
            description = "Creates a new user (with login, email, password and isAdmin flag).",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "The user",
                            content = @Content(schema = @Schema(implementation = UserDetails.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid user params (login, email, password, isAdmin) supplied",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing authentication",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "User not authorized (only admins can create users)",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(
            @RequestBody(
                    content = @Content(schema = @Schema(implementation = UserCreate.class)),
                    description = "JSON object representing user parameters",
                    required = true
            ) String body) throws DuplicateRecordException, AccessRightException, BadArgumentException, UnauthorizedException, InsufficientRightsException {
        //authorization: must be admin
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin()) {
            throw new InsufficientRightsException("Only admin can create registrars");
        }

        //extract params from body
        if (body == null || body.isEmpty()) {
            return mandatoryBodyMissingResponse();
        }
        JsonObject newUserData;
        try (JsonReader r = Json.createReader(new StringReader(body))) {
            newUserData = r.readObject();
        }
        String login = readParam("login", newUserData::getString);
        String email = readParam("email", newUserData::getString);
        String password = readParam("password", newUserData::getString);
        boolean isAdmin = readParam("isAdmin", newUserData::getBoolean);
        //create new user
        UserDetails newUser = userManager.createUser(user.getLogin(), login, email, password, isAdmin);
        //return created user
        return Response.ok(newUser).build();
    }

    @Operation(
            summary = "Get user",
            tags = "Users",
            description = "Returns a user by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The user",
                            content = @Content(schema = @Schema(implementation = UserDetails.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid user ID supplied",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing authentication",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "User not authenticated or not authorized (only admins or the user himself can get user data)",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "User with given ID not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GET
    @Path("{id}")
    public Response getUserById(
            @Parameter(description = "ID of the user", required = true) @PathParam("id") long id) throws UnknownRecordException, AccessRightException, UnauthorizedException, InsufficientRightsException {
        //authorization: must be admin or the user themself
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin() && user.getId() != id) {
            throw new InsufficientRightsException("Only admin and the user himself can get user data");
        }

        //fetch user
        UserDetails userDetails = userManager.getUser(user.getLogin(), id);
        //return user
        return Response.ok(userDetails).build();
    }

    @Operation(
            summary = "Get all users",
            tags = "Users",
            description = "Returns a list of all users.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of users",
                            content = @Content(schema = @Schema(implementation = UserList.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing authentication",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "User not authorized (only admins can get list of users)",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GET
    public Response getUsers() throws AccessRightException, UnauthorizedException, InsufficientRightsException {
        //authorization: must be admin
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin()) {
            throw new InsufficientRightsException("Only admin can get list of users");
        }

        //fetch users
        List<UserDetails> users = userManager.getUsers(user.getLogin());
        //return users
        return Response.ok(new UserList(users)).build();
    }

    @Operation(
            summary = "Update user (except for password)",
            tags = "Users",
            description = "Updates user (login, email, isAdmin) by user ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "The updated user",
                            content = @Content(schema = @Schema(implementation = UserDetails.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid user params (login, email, isAdmin) supplied",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing authentication",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "User not authorized (only admins can update users' records)",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "User with given ID not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(
            @Parameter(description = "ID of the user", required = true) @PathParam("id") long id,
            @RequestBody(
                    content = @Content(schema = @Schema(implementation = UserUpdate.class)),
                    description = "JSON object representing user parameters",
                    required = true
            ) String body) throws UnknownRecordException, DuplicateRecordException, AccessRightException, BadArgumentException, InsufficientRightsException, UnauthorizedException {
        //authorization: must be admin
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin()) {
            throw new InsufficientRightsException("Only admin can update users' records");
        }

        //extract params from body
        if (body == null || body.isEmpty()) {
            throw new BadRequestException("Missing mandatory body");
        }
        JsonObject root;
        try (JsonReader r = Json.createReader(new StringReader(body))) {
            root = r.readObject();
        }
        String login = readParam("login", root::getString);
        String email = readParam("email", root::getString);
        boolean isAdmin = readParam("isAdmin", root::getBoolean);
        //update user
        UserDetails userUpdated = userManager.updateUser(user.getLogin(), id, login, email, isAdmin);
        //return updated user
        return Response.ok(userUpdated).build();
    }

    @Operation(
            summary = "Update user's password",
            tags = "Users",
            description = "Updates user's password (by user ID).",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "The updated user",
                            content = @Content(schema = @Schema(implementation = UserDetails.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid new password supplied",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing authentication",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "User not authorized (only admins and the users themself can update password)",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "User with given ID not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @PUT
    @Path("/{id}/password")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response updateUserPassword(
            @Parameter(description = "ID of the user", required = true) @PathParam("id") long id,
            @RequestBody(
                    //content = @Content(schema = @Schema(implementation = String.class)),
                    description = "New password in plain text",
                    required = true
            ) String newPassword) throws UnknownRecordException, AccessRightException, BadArgumentException, UnauthorizedException, InsufficientRightsException {
        //authorization: must be admin or the user themself
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin() && user.getId() != id) {
            throw new InsufficientRightsException("User " + user.getLogin() + " with id " + user.getId()
                    + " is not authorized to update password for user ID " + id + ". Must be admin or the user themself");
        }

        //extract new password from body
        if (newPassword == null || newPassword.isEmpty()) {
            throw new BadRequestException("Missing mandatory body containing the new password");
        }
        //update user password
        UserDetails userUpdated = userManager.updateUserPassword(user.getLogin(), id, newPassword);
        //return updated user
        return Response.ok(userUpdated).build();
    }

    @Operation(
            summary = "Delete user",
            tags = "Users",
            description = "Deletes a user by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "204", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing authentication",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "User not authorized (only admins can delete users)",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "User with given ID not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") long id) throws UnknownRecordException, AccessRightException, InsufficientRightsException, UnauthorizedException {
        //authorization: must be admin
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin()) {
            throw new InsufficientRightsException("Only admin can delete users");
        }

        //delete user
        userManager.deleteUser(user.getLogin(), id);
        //return nothing
        return Response.noContent().build();
    }

    @Operation(
            summary = "Assign to the user an access-right to manage a registrar",
            tags = "Users",
            description = "Adds given registrar to the list of registrars managed by the user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The right to manage the registrar had already been assigned to the user"),
                    @ApiResponse(responseCode = "201", description = "The right to manage the registrar has been assigned to the user",
                            content = @Content(schema = @Schema(implementation = UserDetails.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing authentication",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "User not authorized (only admins can assign registrar rights to users)",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "User or registrar with given ID not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @POST
    @Path("/{id}/registrar_rights/{registrarCode}")
    public Response giveUserAccessRightToRegistrar(@PathParam("id") long userId, @PathParam("registrarCode") String registrarCode)
            throws UnknownRecordException, AccessRightException, UnauthorizedException, InsufficientRightsException {
        //authorization: must be admin
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin()) {
            throw new InsufficientRightsException("Only admin can assign registrar rights to users");
        }

        //update user
        UserDetails userDetails = userManager.addRegistrarRight(user.getLogin(), userId, registrarCode);
        if (userDetails == null) {
            return Response.status(Response.Status.OK)
                    .entity(new ApiError("User already manages registrar with code: " + registrarCode))
                    .build();
        }
        //return updated user
        return Response.ok(userDetails).build();
    }

    @Operation(
            summary = "List user's access-rights to registrars",
            tags = "Users",
            description = "Returns list of registrars managed by the user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of registrars managed by the user",
                            content = @Content(schema = @Schema(implementation = List.class))),
                    //content = @Content(schema = @Schema(implementation = RegistrarList.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing authentication",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "User not authorized (only admins and the user himself can get list of registrars managed by the user)",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "User with given ID not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GET
    @Path("/{id}/registrar_rights")
    public Response listUsersAccessRightsToRegistrars(@PathParam("id") long userId)
            throws UnknownRecordException, AccessRightException, InsufficientRightsException, UnauthorizedException {
        //authorization: must be admin or the user themself
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin() && user.getId() != userId) {
            throw new InsufficientRightsException("Only admin and the user themself can get list of registrars managed by the user");
        }

        //update user
        List<String> rights = userManager.getRegistrarRights(user.getLogin(), userId);
        //return updated user
        return Response.ok(rights).build();
    }

    @Operation(
            summary = "Remove user's access-right to the registrar",
            tags = "Users",
            description = "Removes given registrar from the list of registrars managed by the user.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Success",
                            content = @Content(schema = @Schema(implementation = UserDetails.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing authentication",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "User not authorized (only admins can remove registrar rights from users)",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "User or registrar with given ID not found. Or the user did not have right to manage the registrar",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @DELETE
    @Path("/{id}/registrar_rights/{registrarCode}")
    public Response removeUsersAccessRightToRegistrar(@PathParam("id") long userId, @PathParam("registrarCode") String registrarCode)
            throws UnknownRecordException, AccessRightException, UnauthorizedException, InsufficientRightsException {
        //authorization: must be admin
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        if (!user.isAdmin()) {
            throw new InsufficientRightsException("Only admin can remove registrar rights from users");
        }

        //update user
        UserDetails userDetails = userManager.removeRegistrarRight(user.getLogin(), userId, registrarCode);
        //return updated user
        return Response.ok(userDetails).build();
    }

    record UserCreate(@NotNull String login, @NotNull String email, @NotNull String password,
                      @NotNull boolean isAdmin) {
    }

    record UserUpdate(@NotNull String login, @NotNull String email, @NotNull boolean isAdmin) {
    }

}

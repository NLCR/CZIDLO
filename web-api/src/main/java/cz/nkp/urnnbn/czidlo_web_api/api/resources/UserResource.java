package cz.nkp.urnnbn.czidlo_web_api.api.resources;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.czidlo_web_api.api.ApiError;
import cz.nkp.urnnbn.czidlo_web_api.api.AuthenticatedUserPrincipal;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.AccessRightException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.InsufficientRightsException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnauthorizedException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import cz.nkp.urnnbn.czidlo_web_api.api.users.core.UserDetails;
import cz.nkp.urnnbn.czidlo_web_api.api.users.user_manager.UserManager;
import cz.nkp.urnnbn.czidlo_web_api.api.users.user_manager.UserManagerImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/user")
public class UserResource extends AbstractResource {

    @Context
    private SecurityContext securityContext;

    //private static final UserManager userManager = new UserManagerMockInMemory();
    private static final UserManager userManager = new UserManagerImpl();

    @Operation(
            summary = "Get this user (from Authentication header)",
            tags = "Users",
            description = "Returns information about this user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The user",
                            content = @Content(schema = @Schema(implementation = UserDetails.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing authentication",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GET
    public Response getThisUser() throws UnknownRecordException, UnauthorizedException, InsufficientRightsException {
        //authorization: must be logged in
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();

        try {
            UserDetails userDetails = userManager.getUser(user.getLogin(), user.getLogin());
            return Response.ok(userDetails).build();
        } catch (AccessRightException e) {
            throw new RuntimeException(e);
        }
    }
}

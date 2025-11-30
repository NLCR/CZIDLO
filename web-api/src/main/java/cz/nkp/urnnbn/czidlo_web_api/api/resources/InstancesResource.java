package cz.nkp.urnnbn.czidlo_web_api.api.resources;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.czidlo_web_api.api.ApiError;
import cz.nkp.urnnbn.czidlo_web_api.api.AuthenticatedUserPrincipal;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.InstanceManager;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.InstanceManagerImpl;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.Record;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.InsufficientRightsException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnauthorizedException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/instances")
public class InstancesResource extends AbstractResource {

    @Context
    private SecurityContext securityContext;

    private static final InstanceManager instanceManager = new InstanceManagerImpl();

    @Operation(
            summary = "Update instance",
            tags = "Documents",
            description = "Updates digital instance identified by the given ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Updated"),
                    @ApiResponse(responseCode = "400", description = "Invalid digital instance ID format or invalid input data",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights to deactivate digital instance",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Digital instance not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "409", description = "Digital instance is already deactivated",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @PUT
    @Path("{diId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateDigitalInstance(
            @Parameter(description = "Digital instance id (numeric)", required = true)
            @PathParam("diId") String diId,
            @RequestBody(
                    content = @Content(schema = @Schema(implementation = InstanceUpdate.class)),
                    description = "JSON object representing the updated instance data",
                    required = true
            ) String body) throws UnknownRecordException, UnauthorizedException, InsufficientRightsException {
        //authorization: must be admin or user with right to manage registrar of the digital library hosting this digital instance
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        //TODO: implement

        //TODO: uzivatel musi mit prava k registratorovi. Ale ne nute tomu, ktery registroval DD, ale k tomu, ktery ma DI ve své digitální knihovně.
        return Response.status(Response.Status.BAD_REQUEST).entity("Not implemented yet").build();
    }

    @Operation(
            summary = "Deactivate instance",
            tags = "Documents",
            description = "Deactivates digital instance identified by the given ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Deactivated"),
                    @ApiResponse(responseCode = "400", description = "Invalid digital instance ID format",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient rights to deactivate digital instance",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Digital instance not found",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "409", description = "Digital instance is already deactivated",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @POST
    @Path("{diId}/deactivation")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response deactivateDigitalInstance(
            @Parameter(description = "Digital instance id (numeric)", required = true)
            @PathParam("diId") String diId) throws UnknownRecordException, UnauthorizedException, InsufficientRightsException {
        //authorization: must be admin or user with right to manage registrar of the digital library hosting this digital instance
        AuthenticatedUserPrincipal principal = requireUserPrincipal(securityContext);
        User user = principal.getUser();
        //TODO: implement

        //TODO: uzivatel musi mit prava k registratorovi. Ale ne nute tomu, ktery registroval DD, ale k tomu, ktery ma DI ve své digitální knihovně.
        return Response.status(Response.Status.BAD_REQUEST).entity("Not implemented yet").build();
    }

    record InstanceUpdate(@NotNull String url, String format, String accessibility, String accessRestriction) {
    }

}

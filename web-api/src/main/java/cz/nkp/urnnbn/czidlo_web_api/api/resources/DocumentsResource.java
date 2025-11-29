package cz.nkp.urnnbn.czidlo_web_api.api.resources;

import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.czidlo_web_api.api.ApiError;
import cz.nkp.urnnbn.czidlo_web_api.api.AuthenticatedUserPrincipal;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.DocumentManager;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.DocumentManagerImpl;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.Record;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.ConflictException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.InsufficientRightsException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnauthorizedException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/documents")
public class DocumentsResource extends AbstractResource {

    @Context
    private SecurityContext securityContext;

    private static final DocumentManager documentManager = new DocumentManagerImpl();

    @Operation(
            summary = "Fetch document by URN:NBN",
            tags = "Documents",
            description = "Returns digital document identified by the given URN:NBN.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Digital document"),
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
            summary = "Deactivates URN:NBN",
            tags = "Documents",
            description = "Deactivates the given URN:NBN identifier and thus the associated digital document.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Deactivated, i.e. deactivation record created",
                            content = @Content(schema = @Schema(implementation = Record.class))),
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

}

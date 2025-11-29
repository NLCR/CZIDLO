package cz.nkp.urnnbn.czidlo_web_api.api.resources;

import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.czidlo_web_api.api.ApiError;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.DocumentManager;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.DocumentManagerImpl;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.Document;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.Record;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.json.JsonObject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
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
}

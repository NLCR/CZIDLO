package cz.nkp.urnnbn.czidlo_web_api.api.resources;

import cz.nkp.urnnbn.czidlo_web_api.WebApiModuleConfiguration;
import cz.nkp.urnnbn.czidlo_web_api.api.ApiError;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.BadArgumentException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnauthorizedException;
import cz.nkp.urnnbn.indexer.IndexerConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Path("/es")
public class ElasticSearchResource extends AbstractResource {

    @Context
    private SecurityContext securityContext;

    @Operation(
            summary = "Query SEARCH index",
            tags = "Elastic",
            description = "Searches in ElasticSearch, index SEARCH. The request body must contain valid ElasticSearch query in JSON format. The response contains search results in the same format as returned by ElasticSearch.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Search results",
                            content = @Content()),
                    @ApiResponse(responseCode = "400", description = "Invalid input data in request body",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @POST
    @Path("/index_search/_search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(
            @RequestBody(
                    content = @Content(),
                    description = "Query data",
                    required = true
            ) String body) throws UnauthorizedException, BadArgumentException {
        //authorization: even unauthenticated user can search
        //requireUserPrincipal(securityContext);

        //check mandatory body and that it is valid JSON
        if (body == null || body.isEmpty()) {
            return mandatoryBodyMissingResponse();
        }
        checkBodyIsJson(body);

        //System.out.println("Received ElasticSearch query: " + root);
        String index = indexerConfig().getEsApiIndexSearchName();
        return makeEsPostRequest(index, body);
    }

    private IndexerConfig indexerConfig() {
        return WebApiModuleConfiguration.instanceOf().getIndexerConfig();
    }

    private Response makeEsPostRequest(String esIndex, String body) {
        String esBaseUrl = indexerConfig().getEsApiBaseUrl();
        if (esBaseUrl == null || esBaseUrl.isBlank()) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\":\"ES_BASE_URL is not configured\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        if (esIndex == null || esIndex.isBlank()) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\":\"ES_INDEX is not configured\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        URI target = URI.create(esBaseUrl + "/" + esIndex + "/_search");
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();

        HttpRequest req = HttpRequest.newBuilder(target)
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .header("Authorization", basicAuthHeader())
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> esResp;
        try {
            esResp = client.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (java.net.http.HttpTimeoutException e) {
            return Response.status(Response.Status.BAD_GATEWAY)
                    .entity("{\"message\":\"Elasticsearch timeout\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_GATEWAY)
                    .entity("{\"message\":\"Elasticsearch request failed\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        int status = esResp.statusCode();
        String respBody = esResp.body() == null ? "" : esResp.body();

        return Response.status(status)
                .entity(respBody)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private void checkBodyIsJson(String body) throws BadArgumentException {
        try (JsonReader r = Json.createReader(new StringReader(body))) {
            r.readObject();
        } catch (Exception e) {
            throw new BadArgumentException("Request body must be a valid JSON object: " + e.getMessage());
        }
    }

    private String basicAuthHeader() {
        String username = indexerConfig().getEsApiLogin();
        String password = indexerConfig().getEsApiPassword();
        String auth = username + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }


}

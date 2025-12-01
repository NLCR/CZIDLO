package cz.nkp.urnnbn.czidlo_web_api.api.resources;

import cz.nkp.urnnbn.czidlo_web_api.api.ApiError;
import cz.nkp.urnnbn.czidlo_web_api.api.logs.SystemLogProvider;
import cz.nkp.urnnbn.czidlo_web_api.api.logs.SystemLogProviderMock;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

@Path("/system_logs")
public class SystemLogsResource extends AbstractResource {

    private final SystemLogProvider logProvider = new SystemLogProviderMock();

    @Operation(
            summary = "Get system logs",
            tags = "Logs",
            description = "Returns system logs as plain text, possibly limited to the last N lines.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "System logs successfully retrieved",
                            content = {
                                    @Content(mediaType = MediaType.TEXT_PLAIN,
                                            schema = @Schema(implementation = String.class)),
                                    @Content(mediaType = MediaType.APPLICATION_JSON,
                                            schema = @Schema(implementation = LogResponse.class)),
                                    @Content(mediaType = MediaType.APPLICATION_XML,
                                            schema = @Schema(implementation = LogResponse.class))
                            }
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameter value",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "403", description = "User not authenticated or not authorized (only admins can get system logs)",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GET
    //@Produces({MediaType.TEXT_PLAIN, "application/json;qs=1.0", "application/xml;qs=0.5"})
    @Produces({
            "text/plain;qs=1.0",
            "application/json;qs=0.8",
            "application/xml;qs=0.6"
    })
    public Response getSystemLogs(
            @Parameter(
                    description = "Maximum number of log lines to return. If omitted, all available logs are returned.",
                    example = "100"
            )
            @QueryParam("maxLines") String maxLinesStr,
            @Parameter(
                    description = "Oldest date to be included for filtering logs. Format: YYYY-MM-DD.",
                    example = "2025-10-01"
            )
            @QueryParam("minDate") String minDateStr,
            @Parameter(
                    description = "Day after the maximum date to be included for filtering logs. Format: YYYY-MM-DD.",
                    example = "2025-11-01"
            )
            @QueryParam("dayAfterMaxDate") String dayAfterMaxDateStr,

            @Context HttpHeaders headers) {
        //TODO: povoleno jen adminovi
        Integer maxLines = null;
        if (maxLinesStr != null && !maxLinesStr.trim().isEmpty()) {
            try {
                maxLines = Integer.parseInt(maxLinesStr.trim());
            } catch (NumberFormatException e) {
                throw new BadRequestException("Parameter maxLines is not a valid integer: " + maxLinesStr, e);
            }
            if (maxLines <= 0) {
                throw new BadRequestException("Parameter maxLines must be a positive integer: " + maxLines);
            }
        }
        LocalDate minDate = null;
        if (minDateStr != null && !minDateStr.trim().isEmpty()) {
            if (!minDateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                throw new BadRequestException("Parameter minDate is not in the correct format YYYY-MM-DD: " + minDateStr);
            }
            minDate = LocalDate.parse(minDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        LocalDate dayAfterMaxDate = null;
        if (dayAfterMaxDateStr != null && !dayAfterMaxDateStr.trim().isEmpty()) {
            if (!dayAfterMaxDateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                throw new BadRequestException("Parameter dayAfterMaxDate is not in the correct format YYYY-MM-DD: " + dayAfterMaxDateStr);
            }
            dayAfterMaxDate = LocalDate.parse(dayAfterMaxDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        if (minDate != null && dayAfterMaxDate != null) {
            if (!minDate.isBefore(dayAfterMaxDate)) {
                throw new BadRequestException("Parameter minDate must be before dayAfterMaxDate: minDate=" + minDate + ", dayAfterMaxDate=" + dayAfterMaxDate);
            }
        }

        String systemLogs = logProvider.getLogs(maxLines, minDate, dayAfterMaxDate);
        // Akceptované typy seřazené podle q z hlavičky Accept
        List<MediaType> accept = headers.getAcceptableMediaTypes();

        // 1) Pokud klient připouští text/plain (nebo */*), vrať text
        if (accept.stream().anyMatch(mt -> mt.isWildcardType() || mt.isCompatible(MediaType.TEXT_PLAIN_TYPE))) {
            return Response.ok(systemLogs, MediaType.TEXT_PLAIN).build();
        }

        // 2) Jinak, pokud výslovně chce JSON/XML, dej mu je
        if (accept.stream().anyMatch(mt -> mt.isCompatible(MediaType.APPLICATION_JSON_TYPE))) {
            return Response.ok(new LogResponse(systemLogs), MediaType.APPLICATION_JSON).build();
        }
        if (accept.stream().anyMatch(mt -> mt.isCompatible(MediaType.APPLICATION_XML_TYPE))) {
            return Response.ok(new LogResponse(systemLogs), MediaType.APPLICATION_XML).build();
        }

        // 3) Fallback – měl by být zbytečný díky @Produces
        return Response.ok(systemLogs, MediaType.TEXT_PLAIN).build();
    }

    @XmlRootElement(name = "logs")
    public static class LogResponse {
        public String logs;

        public LogResponse() {
        }

        public LogResponse(String logs) {
            this.logs = logs;
        }
    }
}

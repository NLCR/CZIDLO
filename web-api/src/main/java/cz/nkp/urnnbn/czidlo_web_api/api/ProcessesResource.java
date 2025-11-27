package cz.nkp.urnnbn.czidlo_web_api.api;

import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.ProcessType;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.process_manager.ProcessManager;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.process_manager.ProcessManagerMockInMemory;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.process_manager.ProcessInMemoryOutputFile;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.Process;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.core.ProcessList;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.AccessRightException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.InvalidStateException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.json.*;
import jakarta.json.stream.JsonParsingException;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Path("/processes")
public class ProcessesResource extends AbstractResource {

    private static final ProcessManager processManager = new ProcessManagerMockInMemory();

    @Operation(
            summary = "Create process",
            tags = "Processes",
            description = "Schedules a new process. The request body must be a JSON array of strings representing process parameters. The number and meaning of parameters depend on the process type.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "The process",
                            content = @Content(schema = @Schema(implementation = Process.class))),
                    @ApiResponse(responseCode = "400", description = "Unknown process type or invalid process params in request body",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProcess(
            @RequestBody(
                    content = @Content(schema = @Schema(implementation = ProcessCreate.class)),
                    description = "JSON array of strings representing process parameters",
                    required = true
            ) String body) {
        String user = "dummyUser"; // In real scenario, extract from auth context

        if (body == null || body.isEmpty()) {
            return mandatoryBodyMissingResponse();
        }

        JsonObject root;
        try (JsonReader r = Json.createReader(new StringReader(body))) {
            root = r.readObject();
        } catch (JsonParsingException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError("Malformed JSON in request body: " + e.getMessage())).build();
        }

        String typeName;
        try {
            typeName = readParam("type", root::getString);
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError(e.getMessage())).build();
        }


        ProcessType type;
        try {
            type = ProcessType.valueOf(typeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError("Unrecognized type: " + typeName)).build();

        }

        JsonValue paramsJsonValue = root.get("params");
        if (paramsJsonValue.getValueType() != JsonValue.ValueType.OBJECT) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError("Params must be a JSON object")).build();
        }
        JsonObject params = paramsJsonValue.asJsonObject();
        /*
        //TODO: checking if all values are supported types
        if (!params.stream().allMatch(value -> value.getValueType() == JsonValue.ValueType.STRING)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError("Invalid body content: all parameters must be strings, found: " + params)).build();
        }
        String[] paramsValues = params.getValuesAs(JsonString.class).stream().map(JsonString::getString).toArray(String[]::new);
        */

        Map<String, Object> paramsMap = paramsToMap(params);
        Process p = processManager.scheduleNewProcess(user, type, paramsMap);
        return Response.ok(p).build();
    }

    private Map<String, Object> paramsToMap(JsonObject params) {
        Map<String, Object> result = new HashMap<>();
        for (String key : params.keySet()) {
            JsonValue jsonValue = params.get(key);
            Object value = switch (jsonValue.getValueType()) {
                case STRING -> params.getString(key);
                case TRUE -> Boolean.TRUE;
                case FALSE -> Boolean.FALSE;
                case NUMBER -> {
                    JsonNumber num = params.getJsonNumber(key);
                    yield num.isIntegral() ? num.intValue() : num.doubleValue();
                }
                case NULL -> null;
                default ->
                        throw new IllegalArgumentException("Unsupported parameter type ( " + jsonValue.getValueType() + ") for key: " + key);
            };
            result.put(key, value);
        }
        return result;
    }

    @Operation(
            summary = "Get process",
            tags = "Processes",
            description = "Returns a process by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "The process",
                            content = @Content(schema = @Schema(implementation = Process.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Process not found or ID is not integer"),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GET
    @Path("{id}")
    public Response getProcessById(@PathParam("id") long id) {
        String user = "dummyUser"; // In real scenario, extract from auth context
        try {
            Process p = processManager.getProcess(user, id);
            return Response.ok(p).build();
        } catch (UnknownRecordException e) {
            return processNotFounResponse(id);
        } catch (AccessRightException e) {
            return processAccessForbiddenResponse(user, id);
        } catch (Exception e) {
            return internalErrorResponse(e);
        }
    }

    @Operation(
            summary = "Get all processes",
            tags = "Processes",
            description = "Returns a list of all processes.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of processes",
                            content = @Content(schema = @Schema(implementation = ProcessList.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GET
    public Response getAllProcesses() {
        //TODO: až napojíme identifikaci uživatele, tak se změní logika: pokud je uživatel admin, vrátí se všechny procesy, pokud je běžný uživatel, vrátí se jen jeho procesy
        List<Process> p = processManager.getProcesses();
        return Response.ok(new ProcessList(p)).build();
    }

    @Operation(
            summary = "Get all processes by owner",
            tags = "Processes",
            description = "Returns a list of all processes filtered by owner.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of processes",
                            content = @Content(schema = @Schema(implementation = ProcessList.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GET
    @Path("/by-owner/{owner}")
    public Response getProcessByOwner(@PathParam("owner") String owner) {
        String user = "dummyUser"; // In real scenario, extract from auth context
        //TODO: tohle povolíme jen adminovi (až bude implementovaná autentizace)
        List<Process> p = processManager.getProcessesByOwner(owner);
        return Response.ok(p).build();
    }

    private Response processAccessForbiddenResponse(String user, long processId) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(new ApiError("Access denied to user " + user + " for process ID " + processId))
                .build();
    }

    @Operation(
            summary = "Kill running process",
            tags = "Processes",
            description = "Requests killing of a running process by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Success",
                            content = @Content(schema = @Schema(implementation = Boolean.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Process not found or ID is not integer"),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @POST
    @Path("/{id}/kill")
    public Response killRunningProcess(@PathParam("id") long id) {
        String user = "dummyUser"; // In real scenario, extract from auth context
        try {
            boolean p = processManager.killRunningProcess(user, id);
            return Response.ok(p).build();
        } catch (UnknownRecordException e) {
            return processNotFounResponse(id);
        } catch (AccessRightException e) {
            return processAccessForbiddenResponse(user, id);
        } catch (InvalidStateException e) {
            return processInvalidStateResponse(id, "kill");
        } catch (Exception e) {
            return internalErrorResponse(e);
        }
    }

    @Operation(
            summary = "Cancel scheduled process",
            tags = "Processes",
            description = "Requests cancellation of a scheduled process by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Success",
                            content = @Content(schema = @Schema(implementation = Boolean.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Process not found or ID is not integer"),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @POST
    @Path("/{id}/cancel")
    public Response cancelScheduledProcess(@PathParam("id") long id) {
        String user = "dummyUser"; // In real scenario, extract from auth context
        try {
            boolean p = processManager.cancelScheduledProcess(user, id);
            return Response.ok(p).build();
        } catch (UnknownRecordException e) {
            return processNotFounResponse(id);
        } catch (AccessRightException e) {
            return processAccessForbiddenResponse(user, id);
        } catch (InvalidStateException e) {
            return processInvalidStateResponse(id, "cancel");
        } catch (Exception e) {
            return internalErrorResponse(e);
        }
    }

    @Operation(
            summary = "Delete process",
            tags = "Processes",
            description = "Deletes a process by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "204", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Process not found or ID is not integer"),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @DELETE
    @Path("/{id}")
    public Response deleteProcess(@PathParam("id") long id) {
        String user = "dummyUser"; // In real scenario, extract from auth context
        try {
            processManager.deleteProcess(user, id);
            return Response.noContent().build();
        } catch (UnknownRecordException e) {
            return processNotFounResponse(id);
        } catch (AccessRightException e) {
            return processAccessForbiddenResponse(user, id);
        } catch (InvalidStateException e) {
            return processInvalidStateResponse(id, "delete");
        } catch (Exception e) {
            return internalErrorResponse(e);
        }
    }

    @Operation(
            summary = "Get process log",
            tags = "Processes",
            description = "Returns a process log by its ID. Process log is a plain text file.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Process log successfully retrieved",
                            content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Process log not found or ID is not integer"),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GET
    @Path("/{id}/log")
    public Response getProcessLog(@PathParam("id") long id) {
        String user = "dummyUser";
        try {
            FileInputStream processLog = processManager.getProcessLog(user, id);
            return Response.ok(processLog, MediaType.TEXT_PLAIN).build();
        } catch (UnknownRecordException e) {
            return processNotFounResponse(id);
        } catch (AccessRightException e) {
            return processAccessForbiddenResponse(user, id);
        } catch (FileNotFoundException e) {
            return processFileNotFoundResponse(id);
        } catch (Exception e) {
            return internalErrorResponse(e);
        }
    }

    @Operation(
            summary = "Get process output file",
            tags = "Processes",
            description = "Returns a process output file by its ID. The output file can have different mime types depending on the process type.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Process output file successfully retrieved"),
                    @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                            content = @Content(schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(responseCode = "404", description = "Process output file not found or ID is not integer"),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GET
    @Path("/{id}/output")
    public Response getProcessOutput(@PathParam("id") long id) {
        String user = "dummyUser";
        try {
            ProcessInMemoryOutputFile outputFile = processManager.getProcessOutput(user, id);
            Response.ResponseBuilder builder = Response.ok(outputFile.getFile(), outputFile.getMimeType());
            String extension = outputFile.getExtension();
            builder.header("Content-Disposition", "attachment; filename=\"process_" + id + "_output" + extension + "\"");
            return builder.build();
        } catch (UnknownRecordException e) {
            return processNotFounResponse(id);
        } catch (AccessRightException e) {
            return processAccessForbiddenResponse(user, id);
        } catch (InvalidStateException e) {
            return processInvalidStateResponse(id, "fetch-output");
        } catch (FileNotFoundException e) {
            return processFileNotFoundResponse(id);
        } catch (Exception e) {
            return internalErrorResponse(e);
        }
    }

    private Response internalErrorResponse(Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiError("Internal server error: " + e.getMessage()))
                .build();
    }

    private Response mandatoryBodyMissingResponse() {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ApiError("Missing mandatory body"))
                .build();
    }

    private Response mandatoryParamMissingResponse(String paramName) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ApiError("Missing mandatory parameter: " + paramName))
                .build();
    }

    private Response invalidParamValueResponse(String parameterName, String value) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ApiError("Invalid value for parameter " + parameterName + ": " + value))
                .build();
    }

    private Response processNotFounResponse(long processId) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ApiError("Process with ID " + processId + " not found"))
                .build();
    }

    private Response processInvalidStateResponse(long processId, String operationName) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ApiError("Process with ID " + processId + " is not in valid state for operation '" + operationName + "'"))
                .build();
    }

    private Response processInvalidTypeResponse(String type) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ApiError("Unknown process type: " + type))
                .build();
    }

    private Response processFileNotFoundResponse(long processId) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ApiError("File not found for process with ID: " + processId))
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

    record ProcessCreate(@NotNull ProcessType type, @NotNull Object params) {
    }
}

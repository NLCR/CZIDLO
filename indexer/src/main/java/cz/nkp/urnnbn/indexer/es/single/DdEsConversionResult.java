package cz.nkp.urnnbn.indexer.es.single;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.nkp.urnnbn.indexer.es.domain.assigning.AssigningIdx;
import cz.nkp.urnnbn.indexer.es.domain.resolving.ResolvingIdx;
import cz.nkp.urnnbn.indexer.es.domain.searching.SearchingIdx;
import jakarta.json.Json;
import jakarta.json.JsonObject;

import java.util.HashMap;

public class DdEsConversionResult {
    private SearchingIdx search = null;
    private AssigningIdx assignment = null;
    private ResolvingIdx resolve = null;

    public DdEsConversionResult(SearchingIdx search, AssigningIdx assignment) {
        this.search = search;
        this.assignment = assignment;
    }

    public DdEsConversionResult(ResolvingIdx resolve) {
        this.resolve = resolve;
    }

    public SearchingIdx getSearch() {
        return search;
    }

    public AssigningIdx getAssignment() {
        return assignment;
    }

    public ResolvingIdx getResolve() {
        return resolve;
    }

    public JsonObject getSearchJson(ObjectMapper mapper) {
        HashMap<String, Object> map = mapper.convertValue(search, new TypeReference<>() {});
        return Json.createObjectBuilder(map).build();
    }

    public JsonObject getAssignmentJson(ObjectMapper mapper) {
        HashMap<String, Object> map = mapper.convertValue(assignment, new TypeReference<>() {});
        return Json.createObjectBuilder(map).build();
    }

    public JsonObject getResolveJson(ObjectMapper mapper) {
        HashMap<String, Object> map = mapper.convertValue(resolve, new TypeReference<>() {});
        return Json.createObjectBuilder(map).build();
    }

    @Override
    public String toString() {
        return "DdEsConversionResult{" +
                (search != null ? "search=" + search + ", " : "") +
                (assignment != null ? "assignment=" + assignment : "") +
                (resolve != null ? "resolve=" + resolve : "") +
                '}';
    }
}

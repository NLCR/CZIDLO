package cz.nkp.urnnbn.indexer.es.single;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.nkp.urnnbn.indexer.es.domain.assigning.AssigningIdx;
import cz.nkp.urnnbn.indexer.es.domain.searching.SearchingIdx;
import jakarta.json.Json;
import jakarta.json.JsonObject;

import java.util.HashMap;

public class DdEsConversionResult {
    private final SearchingIdx search;
    private final AssigningIdx assignment;

    public DdEsConversionResult(SearchingIdx search, AssigningIdx assignment) {
        this.search = search;
        this.assignment = assignment;
    }

    public SearchingIdx getSearch() {
        return search;
    }

    public AssigningIdx getAssignment() {
        return assignment;
    }

    public JsonObject getSearchJson(ObjectMapper mapper) {
        HashMap<String, Object> map = mapper.convertValue(search, new TypeReference<>() {});
        return Json.createObjectBuilder(map).build();
    }

    public JsonObject getAssignmentJson(ObjectMapper mapper) {
        HashMap<String, Object> map = mapper.convertValue(assignment, new TypeReference<>() {});
        return Json.createObjectBuilder(map).build();
    }

    @Override
    public String toString() {
        return "DdEsConversionResult{" +
                "search=" + search +
                ", assignment=" + assignment +
                '}';
    }
}

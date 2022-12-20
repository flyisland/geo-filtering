package com.solace.demo.geofiltering;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class FilteringResultTest {
    Path workingDir = Path.of("", "src/test/java", "com/solace/demo/geofiltering");

    @Test
    void toJsonString() throws Exception {
        Path file = workingDir.resolve("filtering01.json");
        String jsonString = Files.readString(file);
        var request = FilteringRequest.from(jsonString);
        var result = new FilteringResult(request,100, Range.genesis(request.polygons));
        System.out.println(result.toJsonString());
        assertEquals(1, result.ranges.size());
    }
}
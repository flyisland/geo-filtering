package com.solace.demo.geofiltering;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RangesFinderTest {
    Path workingDir = Path.of("", "src/test/java", "com/solace/demo/geofiltering");

    @ParameterizedTest
    @CsvSource({"intersect001.json, 1"})
    void normalizePolygons(String jsonFile, int expectedCount) throws Exception{
        Path file = workingDir.resolve(jsonFile);
        String jsonString = Files.readString(file);
        var request = FilteringRequest.from(jsonString);

        var result = RangesFinder.normalizePolygons(request.polygons);
        System.out.println(result);
        assertEquals(expectedCount, result.size());
    }
}
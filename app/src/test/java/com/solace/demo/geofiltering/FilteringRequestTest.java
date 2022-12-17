package com.solace.demo.geofiltering;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FilteringRequestTest {
    Path workingDir = Path.of("", "src/test/java", "com/solace/demo/geofiltering");

    @ParameterizedTest
    @CsvSource({
            "intersect001.json, 1",
            "intersect002.json, 2",
            "intersect003.json, 2",
    })
    void normalizePolygons(String jsonFile, int expectedCount) throws Exception {
        Path file = workingDir.resolve(jsonFile);
        String jsonString = Files.readString(file);
        var request = FilteringRequest.from(jsonString);

        var result = Helper.normalizePolygons(request.polygons);
        System.out.println(result);
        assertEquals(expectedCount, result.size());
    }

    @Test
    void from() throws Exception {
        Path file = workingDir.resolve("request01.json");
        String jsonString = Files.readString(file);
        try {
            var request = FilteringRequest.from(jsonString);
            assertEquals(1023, request.maxRangeCount);
            assertEquals(0.67, request.minAccuracy);
            assertEquals(3, request.polygons.size());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
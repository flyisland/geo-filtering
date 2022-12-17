package com.solace.demo.geofiltering;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class FilteringRequestTest {
    Path  workingDir = Path.of("", "src/test/java", "com/solace/demo/geofiltering");
    @Test
    void from() throws Exception{
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
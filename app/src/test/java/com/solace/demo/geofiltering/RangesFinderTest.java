package com.solace.demo.geofiltering;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class RangesFinderTest {
    Path workingDir = Path.of("", "src/test/java", "com/solace/demo/geofiltering");

    @Test
    void find() throws Exception {
        Path file = workingDir.resolve("filtering01.json");
        String jsonString = Files.readString(file);
        try {
            var request = FilteringRequest.from(jsonString);
            var result = RangesFinder.find(request);
            System.out.println(result.ranges);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
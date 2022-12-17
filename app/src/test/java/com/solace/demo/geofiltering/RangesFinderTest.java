package com.solace.demo.geofiltering;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RangesFinderTest {
    Path workingDir = Path.of("", "src/test/java", "com/solace/demo/geofiltering");

    @ParameterizedTest
    @CsvSource({
            "filtering01.json, filtering01.result.txt",
            "filtering02.json, filtering02.result.txt",
    })
    void find(String jsonFile, String expectedFile) throws Exception {
        String jsonString = Files.readString(workingDir.resolve(jsonFile));
        String expected = Files.readString(workingDir.resolve(expectedFile)).trim();
        try {
            var request = FilteringRequest.from(jsonString);
            var result = RangesFinder.find(request);
            assertEquals(expected, result.toJsonString());
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
package com.solace.demo.geofiltering;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class FilteringRequestTest {

    @Test
    void from() {
        var jsonString = "{\"maxRange\":1023,\"accuracy\":67,\"shapes\":[{\"type\":\"Ellipse\","
                + "\"bounds\":{\"south\":-23.361764686950828,\"west\":119.67077923949945,\"north\":-23"
                + ".359506631465173,\"east\":119.6732389218782}},{\"type\":\"Rectangle\",\"bounds\":{\"south\":-23"
                + ".364831421919625,\"west\":119.66936978701817,\"north\":-23.363629832076395,\"east\":119"
                + ".6709791124271}},{\"type\":\"Polygon\",\"coordinates\":[{\"lat\":-23.365481457789006,\"lng\":119"
                + ".67361840609776},{\"lat\":-23.3680618717813,\"lng\":119.67829617861973},{\"lat\":-23"
                + ".366919404604648,\"lng\":119.67853221301304},{\"lat\":-23.36573753165064,\"lng\":119"
                + ".67790994052159},{\"lat\":-23.36522538343291,\"lng\":119.67589291934239},{\"lat\":-23"
                + ".364811723813254,\"lng\":119.6744552553104}]}]}\n";
        try {
            var request = FilteringRequest.from(jsonString);
            assertEquals(1023, request.maxRange);
            assertEquals(67, request.accuracy);
            assertEquals(3, request.polygons.size());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
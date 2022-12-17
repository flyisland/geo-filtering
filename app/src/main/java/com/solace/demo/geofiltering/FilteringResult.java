package com.solace.demo.geofiltering;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class FilteringResult {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    double accuracy;
    List<Range> ranges;

    public double getAccuracy() {
        return accuracy;
    }

    public List<Range> getRanges() {
        return ranges;
    }

    public FilteringResult(double accuracy, List<Range> ranges) {
        this.accuracy = accuracy;
        this.ranges = ranges;
    }

    public String toJsonString() {
        try {
            var json = objectMapper.writeValueAsString(this);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}

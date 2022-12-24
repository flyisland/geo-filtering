package com.solace.demo.geofiltering;

import static com.solace.demo.geofiltering.Constants.DIMS;
import static com.solace.demo.geofiltering.Constants.df;
import static com.solace.demo.geofiltering.Constants.smallestUnit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.List;

public class FilteringResult {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    FilteringRequest request;
    double accuracy;
    String topicPattern;
    List<Range> ranges;

    public double getAccuracy() {
        return accuracy;
    }

    public String getTopicPattern() {
        return topicPattern;
    }

    public List<Range> getRanges() {
        return ranges;
    }

    public FilteringResult(FilteringRequest request, double accuracy, List<Range> ranges) {
        this.request = request;
        this.topicPattern = request.topicPattern;
        this.accuracy = accuracy;
        this.ranges = ranges;
        builtFiltering();
    }

    private void builtFiltering() {
        var scale = smallestUnit.scale();
        for (var range : ranges) {
            range.filtering = new LinkedHashMap<>();
            for (var dim : DIMS.values()) {
                var number = df.get(dim).format(range.sign.get(dim).multiply(range.coord.get(dim)));
                var endCut = (int) Math.round(Math.log10(range.unit.get(dim).doubleValue()));
                endCut = endCut > 0 ? endCut + scale + 1 : endCut + scale;
                range.filtering.put(dim, number.substring(0, number.length() - endCut) + request.singleLevelWildCard);
            }
        }
    }

    public String toJsonString() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toPrettyJsonString() {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}

package com.solace.demo.geofiltering;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class FilteringResult {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HashMap<Range.DIMS, DecimalFormat> df = new HashMap<>();

    static {
        // 5 decimal places
        df.put(Range.DIMS.X, new DecimalFormat("000.00000")); // -180~180
        df.put(Range.DIMS.Y, new DecimalFormat("00.00000")); // -90~90
    }

    FilteringRequest request;
    double accuracy;
    List<Range> ranges;

    public double getAccuracy() {
        return accuracy;
    }

    public List<Range> getRanges() {
        return ranges;
    }

    public FilteringResult(FilteringRequest request, double accuracy, List<Range> ranges) {
        this.request = request;
        this.accuracy = accuracy;
        this.ranges = ranges;
        builtFiltering();
    }

    private void builtFiltering() {
        var scale = Range.smallestUnit.scale();
        for (var range : ranges) {
            range.filtering = new HashMap<>();
            for (var dim : Range.DIMS.values()) {
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

package com.solace.demo.geofiltering;

import java.util.List;

public class FilteringResult {
    double accuracy;
    List<Range> ranges;

    public FilteringResult(double accuracy, List<Range> ranges) {
        this.accuracy = accuracy;
        this.ranges = ranges;
    }
}

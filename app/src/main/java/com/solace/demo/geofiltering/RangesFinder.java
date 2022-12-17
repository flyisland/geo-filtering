package com.solace.demo.geofiltering;

import java.util.Collections;
import java.util.List;
import org.locationtech.jts.geom.Geometry;

public class RangesFinder {

    public static FilteringResult find(FilteringRequest request) {
        List<Range> ranges = Range.genesis(request.polygons);
        double accuracy = 0;
        do {
            ranges.sort(Collections.reverseOrder());
            var splitSuccess = false;
            int i = 0;
            for (Range range : ranges) {
                i++;
                if (range.blankRatio == 0) {
                    break; // no need to continue
                }
                range.split();
                // maxRange is still met after splitting this range
                if (ranges.size() - 1 + range.children.size() <= request.maxRange) {
                    splitSuccess = true;
                    ranges.remove(i);
                    ranges.addAll(range.children);
                    accuracy = calculateAccuracy(ranges, request.polygons);
                }
            }
            if (!splitSuccess) {
                break;
            }
        } while (accuracy < request.minAccuracy);

        return null;
    }

    private static double calculateAccuracy(List<Range> ranges, List<Geometry> targetPolygons) {
        var rangesArea =ranges.stream().mapToDouble(range -> range.xUnit * range.yUnit).sum();
        var targetArea = targetPolygons.stream().mapToDouble(Geometry::getArea).sum();
        return targetArea / rangesArea;
    }
}

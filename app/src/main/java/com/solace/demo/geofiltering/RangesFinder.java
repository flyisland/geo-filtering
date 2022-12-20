package com.solace.demo.geofiltering;

import java.util.Collections;
import java.util.List;
import org.locationtech.jts.geom.Geometry;

public class RangesFinder {

    public static FilteringResult find(FilteringRequest request) {
        request.polygons = Helper.normalizePolygons(request.polygons);
        List<Range> ranges = Range.genesis(request.polygons);
        double accuracy = 0;
        do {
            ranges.sort(Collections.reverseOrder());
            var splitSuccess = false;
            int i = 0;
            for (Range range : ranges) {
                if (range.blankRatio == 0) {
                    break; // no need to continue, since it's perfect match
                }

                // maxRange is still met after splitting this range
                if (range.split() && ranges.size() - 1 + range.children.size() <= request.maxRangeCount) {
                    splitSuccess = true;
                    ranges.remove(i);
                    ranges.addAll(range.children);
                    accuracy = calculateAccuracy(ranges, request.polygons);
                    break;
                }
                i++;
            }
            if (!splitSuccess) {
                // because the maxRangeCount limit
                break;
            }
        } while (accuracy < request.minAccuracy);

        return new FilteringResult(request, accuracy, ranges);
    }

    private static double calculateAccuracy(List<Range> ranges, List<Geometry> targetPolygons) {
        var rangesArea =ranges.stream().mapToDouble(Range::getRectangleArea).sum();
        var targetArea = targetPolygons.stream().mapToDouble(Geometry::getArea).sum();
        return targetArea / rangesArea;
    }
}

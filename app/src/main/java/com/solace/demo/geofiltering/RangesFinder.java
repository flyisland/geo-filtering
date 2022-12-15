package com.solace.demo.geofiltering;

import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Geometry;

public class RangesFinder {

    public static FilteringResult find(FilteringRequest request) {
        return null;
    }

    // union all intersected polygons
    static List<Geometry> normalizePolygons(List<Geometry> _polygons) {
        return unionPolygons(_polygons);
    }

    static List<Geometry> unionPolygons(List<Geometry> _polygons) {
        List<Geometry> result = _polygons;
        boolean foundIntersection;
        do {
            foundIntersection = false;
            ArrayList<Geometry> polygonsToCheck = new ArrayList<>(result);
            result = new ArrayList<>();
            var i = 0;
            while (i < polygonsToCheck.size()) {
                Geometry curtPolygon = polygonsToCheck.get(i);
                var j=i+1;
                while (j < polygonsToCheck.size()) {
                    if(curtPolygon.intersects(polygonsToCheck.get(j))) {
                        curtPolygon=curtPolygon.union(polygonsToCheck.get(j));
                        polygonsToCheck.remove(j);
                        foundIntersection = true;
                    }else{
                        j++;
                    }
                }
                result.add(curtPolygon);
                i++;
            }
        } while (foundIntersection);
        return result;
    }

}

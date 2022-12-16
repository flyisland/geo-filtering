package com.solace.demo.geofiltering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.polygonize.Polygonizer;

public class Helper {
    // union all intersected polygons
    static List<Geometry> normalizePolygons(List<Geometry> _polygons) {
        var polygons = new ArrayList<>(_polygons);
        List<Geometry> nonSelfIntersects = new ArrayList<>();

        // 1. If the geometry is a self intersections' polygon, convert it into a multi-polygon
        for (Geometry polygon : polygons){
            nonSelfIntersects.add(validateSelfIntersection(polygon));
        }
        // 2. If any polygons intersects, union them
        var unionPolygons = unionPolygons(nonSelfIntersects);
        // 3. If there is a multi-polygon, separate it
        List<Geometry> result = new ArrayList<>();
        for (Geometry polygon : unionPolygons){
            for (int i = 0; i < polygon.getNumGeometries(); i++) {
                result.add(polygon.getGeometryN(i));
            }
        }
        return result;
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

    /**
     * Copy from [stackoverflow](https://stackoverflow.com/questions/31473553/is-there-a-way-to-convert-a-self-intersecting-polygon-to-a-multipolygon-in-jts)
     * Get / create a valid version of the geometry given. If the geometry is a polygon or multi polygon, self intersections /
     * inconsistencies are fixed. Otherwise, the geometry is returned.
     */
    static Geometry validateSelfIntersection(Geometry geom){
        if(geom instanceof Polygon){
            if(geom.isValid()){
                geom.normalize(); // validate does not pick up rings in the wrong order - this will fix that
                return geom; // If the polygon is valid just return it
            }
            Polygonizer polygonizer = new Polygonizer();
            addPolygon((Polygon)geom, polygonizer);
            return toPolygonGeometry(polygonizer.getPolygons());
        }else if(geom instanceof MultiPolygon){
            if(geom.isValid()){
                geom.normalize(); // validate does not pick up rings in the wrong order - this will fix that
                return geom; // If the multipolygon is valid just return it
            }
            Polygonizer polygonizer = new Polygonizer();
            for(int n = geom.getNumGeometries(); n-- > 0;){
                addPolygon((Polygon)geom.getGeometryN(n), polygonizer);
            }
            return toPolygonGeometry(polygonizer.getPolygons());
        }else{
            return geom; // In my case, I only care about polygon / multipolygon geometries
        }
    }

    /**
     * Add all line strings from the polygon given to the polygonizer given
     *
     * @param polygon polygon from which to extract line strings
     * @param polygonizer polygonizer
     */
    private static void addPolygon(Polygon polygon, Polygonizer polygonizer){
        addLineString(polygon.getExteriorRing(), polygonizer);
        for(int n = polygon.getNumInteriorRing(); n-- > 0;){
            addLineString(polygon.getInteriorRingN(n), polygonizer);
        }
    }

    /**
     * Add the linestring given to the polygonizer
     *
     * @param lineString line string
     * @param polygonizer polygonizer
     */
    private static void addLineString(LineString lineString, Polygonizer polygonizer){

        if(lineString instanceof LinearRing){ // LinearRings are treated differently to line strings : we need a LineString NOT a LinearRing
            lineString = lineString.getFactory().createLineString(lineString.getCoordinateSequence());
        }

        // union the linestring with the point makes any self intersections explicit.
        Point point = lineString.getFactory().createPoint(lineString.getCoordinateN(0));
        Geometry toAdd = lineString.union(point);

        //Add result to polygonizer
        polygonizer.add(toAdd);
    }

    /**
     * Get a geometry from a collection of polygons.
     *
     * @param polygons collection
     * @return null if there were no polygons, the polygon if there was only one, or a MultiPolygon containing all polygons otherwise
     */
    private static Geometry toPolygonGeometry(Collection<?> polygons){
        switch(polygons.size()){
            case 0:
                return null; // No valid polygons!
            case 1:
                return (Geometry)polygons.iterator().next(); // single polygon - no need to wrap
            default:
                //polygons may still overlap! Need to sym difference them
                Iterator<?> iter = polygons.iterator();
                Geometry ret = (Geometry)iter.next();
                while(iter.hasNext()){
                    ret = ret.symDifference((Geometry)iter.next());
                }
                return ret;
        }
    }
}

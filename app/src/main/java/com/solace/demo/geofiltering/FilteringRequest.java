package com.solace.demo.geofiltering;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.util.GeometricShapeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilteringRequest {
    static final Logger logger = LoggerFactory.getLogger(FilteringRequest.class);

    int maxRangeCount;
    double minAccuracy;
    String singleLevelWildCard;
    List<Geometry> polygons = new ArrayList<>();

    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static FilteringRequest from(String jsonString) throws Exception {
        return from(objectMapper.readTree(jsonString));
    }
    public static FilteringRequest from(byte[] jsonData) throws Exception {
        return from(objectMapper.readTree(jsonData));
    }
    enum ShapeType {
        Ellipse,Rectangle,Polygon
    }

    static class Bounds {
        double north, south, east, west;
        Bounds(JsonNode bounds){
            north = bounds.get("north").asDouble();
            south = bounds.get("south").asDouble();
            east = bounds.get("east").asDouble();
            west = bounds.get("west").asDouble();
        }
    }

    static FilteringRequest from(JsonNode root) {
        var request = new FilteringRequest();
        request.maxRangeCount = root.get("maxRangeCount").asInt();
        request.minAccuracy = root.get("minAccuracy").asDouble()/100;
        request.singleLevelWildCard = root.get("singleLevelWildCard").asText();
        var shapes = root.get("shapes");
        shapes.elements().forEachRemaining((shape) -> {
            var gsf = new GeometricShapeFactory();
            var geomFact = new GeometryFactory();
            var shapeType = shape.get("type").asText();
            if (ShapeType.Ellipse.name().equalsIgnoreCase(shapeType)){
                var bounds = new Bounds(shape.get("bounds"));
                gsf.setWidth(bounds.east-bounds.west);
                gsf.setHeight(bounds.north-bounds.south);
                gsf.setCentre(new Coordinate(
                        bounds.west+(bounds.east-bounds.west)/2,
                        bounds.south+(bounds.north-bounds.south)/2));
                gsf.setNumPoints(100);
                request.polygons.add(gsf.createEllipse());
            }else if(ShapeType.Rectangle.name().equalsIgnoreCase(shapeType)){
                var bounds = new Bounds(shape.get("bounds"));
                Coordinate[] pts = new Coordinate[5];
                pts[0] = new Coordinate(bounds.east, bounds.north);
                pts[1] = new Coordinate(bounds.east, bounds.south);
                pts[2] = new Coordinate(bounds.west, bounds.south);
                pts[3] = new Coordinate(bounds.west, bounds.north);
                pts[4] = new Coordinate(bounds.east, bounds.north);
                request.polygons.add(geomFact.createPolygon(pts));
            }else if(ShapeType.Polygon.name().equalsIgnoreCase(shapeType)){
                var pts=new ArrayList<Coordinate>();
                shape.get("coordinates").elements().forEachRemaining((coord)->
                        pts.add(new Coordinate(coord.get("lng").asDouble(), coord.get("lat").asDouble())));
                pts.add(pts.get(0));
                request.polygons.add(geomFact.createPolygon(pts.toArray(new Coordinate[0])));
            } else{
                logger.error("Unknown shape type: {}", shapeType);
            }
        });

        return request;
    }

    public String toString() {
        var result = new StringBuilder();
        result.append(String.format("maxRangeCount = %d%n",this.maxRangeCount));
        result.append(String.format("minAccuracy = %f%n",this.minAccuracy));
        if (polygons.size() == 0) {
            result.append("polygons = []\n");
        } else {
            result.append("polygons = [\n");
            polygons.forEach(p->result.append(String.format("\t%s%n",p)));
            result.append("]");
        }
        return result.toString();
    }
}

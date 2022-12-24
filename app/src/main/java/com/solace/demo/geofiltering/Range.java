package com.solace.demo.geofiltering;

import static com.solace.demo.geofiltering.Constants.DIMS;
import static com.solace.demo.geofiltering.Constants.NEGATIVE;
import static com.solace.demo.geofiltering.Constants.POSITIVE;
import static com.solace.demo.geofiltering.Constants.TEN;
import static com.solace.demo.geofiltering.Constants.smallestUnit;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

public class Range implements Comparable<Range>, Cloneable {
    private static final GeometryFactory geomFact = new GeometryFactory();

    public Range() {
    }

    public Map<DIMS, BigDecimal> getSign() {
        return sign;
    }

    public Map<DIMS, BigDecimal> getCoord() {
        return coord;
    }

    public Map<DIMS, BigDecimal> getUnit() {
        return unit;
    }

    public Map<DIMS, String> getFiltering() {
        return filtering;
    }

    public void setSign(Map<DIMS, BigDecimal> sign) {
        this.sign = sign;
    }

    public void setCoord(Map<DIMS, BigDecimal> coord) {
        this.coord = coord;
    }

    public void setUnit(Map<DIMS, BigDecimal> unit) {
        this.unit = unit;
    }

    public void setFiltering(Map<DIMS, String> filtering) {
        this.filtering = filtering;
    }

    Map<DIMS, BigDecimal> sign;
    Map<DIMS, BigDecimal> coord;
    Map<DIMS, BigDecimal> unit;
    Map<DIMS, String> filtering;
    double blankArea;
    double blankRatio;
    List<Geometry> polygonsToCover;
    List<Geometry> intersectingPolygons;
    List<Range> children;

    private Range(int xSign, int ySign, double xCoord, double yCoord, double bothUnit, List<Geometry> polygonsToCover) {
        sign = new LinkedHashMap<>();
        coord = new LinkedHashMap<>();
        unit = new LinkedHashMap<>();
        sign.put(DIMS.X, new BigDecimal(xSign));
        sign.put(DIMS.Y, new BigDecimal(ySign));
        coord.put(DIMS.X, new BigDecimal(xCoord));
        coord.put(DIMS.Y, new BigDecimal(yCoord));
        unit.put(DIMS.X, new BigDecimal(bothUnit));
        unit.put(DIMS.Y, new BigDecimal(bothUnit));
        this.polygonsToCover = polygonsToCover;
    }

    public static List<Range> genesis(List<Geometry> polygonsToCover) {
        List<Range> result = new ArrayList<>();
        double unit=100;
        for (var xSign : List.of(POSITIVE, NEGATIVE)) {
            for (var ySign : List.of(POSITIVE, NEGATIVE)) {
                for (var xCoord : List.of(0, 1)) {
                    var range = new Range(xSign, ySign, xCoord*unit, 0, unit, polygonsToCover);
                    range.calculate();
                    if (range.blankRatio < 1) {
                        result.add(range);
                    }
                }
            }
        }
        return result;
    }

    void calculate() {
        if (this.intersectingPolygons != null) {
            // no need to calculate again
            return;
        }
        this.intersectingPolygons = new ArrayList<>();
        var rangeRectangle = builtRangeRectangle();
        double intersectionArea = 0;
        for (var polygon : polygonsToCover) {
            var intersection = rangeRectangle.intersection(polygon);
            if (!intersection.isEmpty() && intersection.getArea() > 0) {
                intersectionArea += intersection.getArea();
                intersectingPolygons.add(intersection);
            }
        }
        var rectangleArea = getRectangleArea();
        blankArea = rectangleArea - intersectionArea;
        blankRatio = blankArea / rectangleArea;
    }

    boolean split() {
        if (children != null) {
            // no need to split again
            return children.size()>0;
        }
        children = new ArrayList<>();
        var env = getIntersectionsEnvelope();
        var xRatio = (env.getMaxX() - env.getMinX()) / unit.get(DIMS.X).doubleValue();
        var yRatio = (env.getMaxY() - env.getMinY()) / unit.get(DIMS.Y).doubleValue();
        DIMS dim = xRatio < yRatio ? DIMS.X : DIMS.Y;
        if (unit.get(dim).compareTo(smallestUnit)==0){
            return false;
        }
        for (var i = 0; i < 10; i++) {
            try {
                var child = (Range) this.clone();
                child.unit.put(dim, unit.get(dim).divide(TEN));
                child.coord.put(dim, this.coord.get(dim).add(child.unit.get(dim).multiply(new BigDecimal(i))));
                child.polygonsToCover = this.intersectingPolygons;
                child.intersectingPolygons = null;
                child.children = null;
                child.calculate();
                if (child.blankRatio < 1) {
                    children.add(child);
                }
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    Double getRectangleArea() {
        return unit.get(DIMS.X).multiply(unit.get(DIMS.Y)).doubleValue();
    }

    private Envelope getIntersectionsEnvelope() {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (Geometry polygon : intersectingPolygons) {
            var env = polygon.getEnvelopeInternal();
            minX = Math.min(env.getMinX(), minX);
            minY = Math.min(env.getMinY(), minY);
            maxX = Math.max(env.getMaxX(), maxX);
            maxY = Math.max(env.getMaxY(), maxY);
        }
        return new Envelope(minX, maxX, minY, maxY);
    }

    private Geometry builtRangeRectangle() {
        Coordinate[] pts = new Coordinate[5];
        final double x1 = sign.get(DIMS.X).multiply(coord.get(DIMS.X)).doubleValue();
        final double y1 = sign.get(DIMS.Y).multiply(coord.get(DIMS.Y)).doubleValue();
        final double x2 = sign.get(DIMS.X).multiply(coord.get(DIMS.X).add(unit.get(DIMS.X))).doubleValue();
        final double y2 = sign.get(DIMS.Y).multiply(coord.get(DIMS.Y).add(unit.get(DIMS.Y))).doubleValue();
        pts[0] = new Coordinate(x1, y1);
        pts[1] = new Coordinate(x1, y2);
        pts[2] = new Coordinate(x2, y2);
        pts[3] = new Coordinate(x2, y1);
        pts[4] = new Coordinate(pts[0]);
        return geomFact.createPolygon(pts);
    }

    @Override
    public int compareTo(Range r2) {
        Range r1 = this;
        if (r1.blankArea != r2.blankArea) {
            return Double.compare(r1.blankArea, r2.blankArea);
        } else {
            return Double.compare(r1.blankRatio, r2.blankRatio);
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Range r = (Range)super.clone();
        r.sign = new LinkedHashMap<>(this.sign);
        r.coord = new LinkedHashMap<>(this.coord);
        r.unit = new LinkedHashMap<>(this.unit);
        return r;
    }
}

package com.solace.demo.geofiltering;

import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

public class Range implements Comparable<Range>, Cloneable {
    static final int POSITIVE = 1;
    static final int NEGATIVE = -1;
    static GeometryFactory geomFact = new GeometryFactory();

    int xSign, ySign;
    double x, y, xUnit, yUnit;
    double blankArea;
    double blankRatio;
    List<Geometry> polygonsToCover;
    List<Geometry> intersectingPolygons;
    List<Range> children;

    private Range(int xSign, int ySign, double x, List<Geometry> polygonsToCover) {
        this.xSign = xSign;
        this.ySign = ySign;
        this.x = x;
        this.y = 0;
        this.xUnit = 100;
        this.yUnit = 100;
        this.polygonsToCover = polygonsToCover;
    }

    public static List<Range> genesis(List<Geometry> polygonsToCover) {
        List<Range> result = new ArrayList<>();
        for (var xSign : List.of(POSITIVE, NEGATIVE)) {
            for (var ySign : List.of(POSITIVE, NEGATIVE)) {
                for (var x : List.of(0, 1)) {
                    var range = new Range(xSign, ySign, x, polygonsToCover);
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
        this.intersectingPolygons = new ArrayList<>();
        var rangeRectangle = builtRangeRectangle();
        double intersectionArea = 0;
        for (var polygon : polygonsToCover) {
            var intersection = rangeRectangle.intersection(polygon);
            if (!intersection.isEmpty() && intersection.getArea() > 0) {
                intersectionArea += intersection.getArea();
                intersectingPolygons.add(polygon);
            }
        }
        var rectangleArea = xUnit * yUnit;
        blankArea = rectangleArea - intersectionArea;
        blankRatio = blankArea / rectangleArea;
    }

    void split() {
        children = new ArrayList<>();
        var env = getIntersectionsEnvelope();
        var xRatio = (env.getMinX() - env.getMinX()) / xUnit;
        var yRatio = (env.getMaxY() - env.getMaxY()) / yUnit;
        try {
            for (var i = 0; i < 10; i++) {
                var child = (Range) this.clone();
                if (xRatio < yRatio) {
                    child.xUnit = xUnit / 10;
                    child.x = this.x + child.xUnit * i;
                } else {
                    child.yUnit = yUnit / 10;
                    child.y = this.y + child.yUnit * i;
                }
                child.polygonsToCover = this.intersectingPolygons;
                child.intersectingPolygons = null;
                child.calculate();
                if (child.blankRatio < 1) {
                    children.add(child);
                }
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }


    private Envelope getIntersectionsEnvelope() {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
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
        pts[0] = new Coordinate(xSign * x, ySign * y);
        pts[1] = new Coordinate(xSign * x, ySign * (y + yUnit));
        pts[2] = new Coordinate(xSign * (x + xUnit), ySign * (y + yUnit));
        pts[3] = new Coordinate(xSign * (x + xUnit), ySign * y);
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
        return super.clone();
    }
}

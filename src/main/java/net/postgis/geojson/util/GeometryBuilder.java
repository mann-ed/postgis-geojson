package net.postgis.geojson.util;

import java.util.ArrayList;
import java.util.List;

import net.postgis.jdbc.geometry.LinearRing;
import net.postgis.jdbc.geometry.Point;
import net.postgis.jdbc.geometry.Polygon;

public class GeometryBuilder {
    public static final int DEFAULT_SRID = 4326;

    private GeometryBuilder() {
        // Don't do anything
    }

    public static Point[] createPoints(final double[] points) {
        final List<Point> result = new ArrayList<>();

        for (int i = 0; i < points.length; i += 2) {
            result.add(new Point(points[i], points[i + 1]));
        }

        return result.toArray(new Point[0]);
    }

    public static Polygon createPolygon(final Point[] points) {
        final Polygon result = new Polygon(new LinearRing[] { new LinearRing(points) });
        result.setSrid(DEFAULT_SRID);
        return result;
    }

    public static Polygon createPolygon(final Point[] points, final int srid) {
        final Polygon result = new Polygon(new LinearRing[] { new LinearRing(points) });
        result.setSrid(srid);
        return result;
    }

    public static Point createPoint(final double x, final double y) {
        final Point point = new Point(x, y);
        point.setSrid(DEFAULT_SRID);
        return point;
    }

    public static Point createPoint(final double x, final double y, final int srid) {
        final Point point = new Point(x, y);
        point.setSrid(srid);
        return point;
    }

    public static Point createPoint3d(final double x, final double y, final double z) {
        final Point point = new Point(x, y, z);
        point.setSrid(DEFAULT_SRID);
        return point;
    }

    public static Point createPoint3d(final double x, final double y, final double z, final int srid) {
        final Point point = new Point(x, y, z);
        point.setSrid(srid);
        return point;
    }
}

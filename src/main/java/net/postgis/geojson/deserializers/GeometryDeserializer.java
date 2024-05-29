package net.postgis.geojson.deserializers;

import static net.postgis.geojson.GeometryTypes.LINE_STRING;
import static net.postgis.geojson.GeometryTypes.MULTI_LINE_STRING;
import static net.postgis.geojson.GeometryTypes.MULTI_POINT;
import static net.postgis.geojson.GeometryTypes.MULTI_POLYGON;
import static net.postgis.geojson.GeometryTypes.POINT;
import static net.postgis.geojson.GeometryTypes.POLYGON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import net.postgis.jdbc.geometry.Geometry;
import net.postgis.jdbc.geometry.GeometryCollection;
import net.postgis.jdbc.geometry.LineString;
import net.postgis.jdbc.geometry.LinearRing;
import net.postgis.jdbc.geometry.MultiLineString;
import net.postgis.jdbc.geometry.MultiPoint;
import net.postgis.jdbc.geometry.MultiPolygon;
import net.postgis.jdbc.geometry.Point;
import net.postgis.jdbc.geometry.Polygon;

/**
 * Deserializer for Geometry types.
 *
 * @author Maycon Viana Bordin <mayconbordin@gmail.com>
 */
public class GeometryDeserializer extends JsonDeserializer<Geometry> {

    @Override
    public Geometry deserialize(final JsonParser jp, final DeserializationContext dc) throws IOException {
        String fieldName;
        String type = null;

        while (true) {
            fieldName = jp.nextFieldName();

            if (fieldName == null) {
                return null;
            } else if (fieldName.equals("type")) {
                type = jp.nextTextValue();
            } else if (fieldName.equals("coordinates")) {
                final JsonNode node        = jp.readValueAsTree();
                final JsonNode coordinates = node.get("coordinates");

                return coordinatesToGeometry(type, coordinates, jp);
            } else if (fieldName.equals("geometries")) {
                final JsonNode node       = jp.readValueAsTree();
                final JsonNode geometries = node.get("geometries");

                return new GeometryCollection(readNodeAsGeometryArray(geometries, jp));
            }
        }
    }

    protected Geometry coordinatesToGeometry(final String type, final JsonNode coordinates, final JsonParser jp)
            throws JsonParseException {
        switch (type) {
        case POINT:
            return readNodeAsPoint(coordinates);
        case LINE_STRING:
            return readNodeAsLineString(coordinates);
        case POLYGON:
            return new Polygon(readNodeAsLinearRingArray(coordinates));
        case MULTI_POINT:
            return new MultiPoint(readNodeAsPointArray(coordinates));
        case MULTI_LINE_STRING:
            return new MultiLineString(readNodeAsLineStringArray(coordinates));
        case MULTI_POLYGON:
            return new MultiPolygon(readNodeAsPolygonArray(coordinates));
        default:
            throw new JsonParseException("\"" + type + "\" is not a valid Geometry type.", jp.getCurrentLocation());
        }
    }

    protected Geometry[] readNodeAsGeometryArray(final JsonNode node, final JsonParser jp) throws JsonParseException {
        if (!node.isArray()) {
            return null;
        }

        final List<Geometry>     values = new ArrayList<>();
        final Iterator<JsonNode> it     = node.iterator();

        while (it.hasNext()) {
            final JsonNode val = it.next();
            if (val.isObject()) {
                final Iterator<Map.Entry<String, JsonNode>> fields      = val.fields();
                String                                      type        = null;
                JsonNode                                    coordinates = null;

                while (fields.hasNext()) {
                    final Map.Entry<String, JsonNode> e = fields.next();

                    if (e.getKey().equals("type")) {
                        type = e.getValue().asText();
                    } else if (e.getKey().equals("coordinates")) {
                        coordinates = e.getValue();
                    }
                }

                values.add(coordinatesToGeometry(type, coordinates, jp));
            }
        }

        return values.toArray(new Geometry[values.size()]);
    }

    protected LineString[] readNodeAsLineStringArray(final JsonNode node) {
        if (!node.isArray()) {
            return null;
        }

        final List<LineString>   values = new ArrayList<>();
        final Iterator<JsonNode> it     = node.iterator();

        while (it.hasNext()) {
            final JsonNode val = it.next();
            if (val.isArray()) {
                values.add(readNodeAsLineString(val));
            }
        }

        return values.toArray(new LineString[values.size()]);
    }

    protected LineString readNodeAsLineString(final JsonNode node) {
        final Point[] points = readNodeAsPointArray(node);
        return new LineString(points);
    }

    protected Polygon[] readNodeAsPolygonArray(final JsonNode node) {
        if (!node.isArray()) {
            return null;
        }

        final List<Polygon>      values = new ArrayList<>();
        final Iterator<JsonNode> it     = node.iterator();

        while (it.hasNext()) {
            final JsonNode val = it.next();
            if (val.isArray()) {
                values.add(new Polygon(readNodeAsLinearRingArray(val)));
            }
        }

        return values.toArray(new Polygon[values.size()]);
    }

    protected LinearRing[] readNodeAsLinearRingArray(final JsonNode node) {
        if (!node.isArray()) {
            return null;
        }

        final List<LinearRing>   values = new ArrayList<>();
        final Iterator<JsonNode> it     = node.iterator();

        while (it.hasNext()) {
            final JsonNode val = it.next();
            if (val.isArray()) {
                values.add(readNodeAsLinearRing(val));
            }
        }

        return values.toArray(new LinearRing[values.size()]);
    }

    protected LinearRing readNodeAsLinearRing(final JsonNode node) {
        final Point[] points = readNodeAsPointArray(node);
        return new LinearRing(points);
    }

    protected Point[] readNodeAsPointArray(final JsonNode node) {
        if (!node.isArray()) {
            return null;
        }

        final List<Point>        values = new ArrayList<>();
        final Iterator<JsonNode> it     = node.iterator();

        while (it.hasNext()) {
            final JsonNode val = it.next();
            if (val.isArray()) {
                values.add(readNodeAsPoint(val));
            }
        }

        return values.toArray(new Point[values.size()]);
    }

    protected Point readNodeAsPoint(final JsonNode node) {
        if (!node.isArray()) {
            return null;
        }

        final List<Double>       values = new ArrayList<>();
        final Iterator<JsonNode> it     = node.iterator();

        while (it.hasNext()) {
            values.add(it.next().asDouble());
        }

        return new Point(values.get(0), values.get(1), values.size() > 2 ? values.get(2) : 0.0);
    }
}

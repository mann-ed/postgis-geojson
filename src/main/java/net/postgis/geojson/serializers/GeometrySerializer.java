package net.postgis.geojson.serializers;

import static net.postgis.geojson.GeometryTypes.GEOMETRY_COLLECTION;
import static net.postgis.geojson.GeometryTypes.LINE_STRING;
import static net.postgis.geojson.GeometryTypes.MULTI_LINE_STRING;
import static net.postgis.geojson.GeometryTypes.MULTI_POINT;
import static net.postgis.geojson.GeometryTypes.MULTI_POLYGON;
import static net.postgis.geojson.GeometryTypes.POINT;
import static net.postgis.geojson.GeometryTypes.POLYGON;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import net.postgis.jdbc.geometry.Geometry;
import net.postgis.jdbc.geometry.GeometryCollection;
import net.postgis.jdbc.geometry.LineString;
import net.postgis.jdbc.geometry.MultiLineString;
import net.postgis.jdbc.geometry.MultiPoint;
import net.postgis.jdbc.geometry.MultiPolygon;
import net.postgis.jdbc.geometry.Point;
import net.postgis.jdbc.geometry.Polygon;

/**
 * Serializer for Geometry types.
 *
 * @author Maycon Viana Bordin <mayconbordin@gmail.com>
 */
public class GeometrySerializer extends JsonSerializer<Geometry> {

    @Override
    public void serialize(final Geometry geom, final JsonGenerator json, final SerializerProvider provider)
            throws IOException {
        json.writeStartObject();

        if (geom instanceof Point) {
            serializePoint((Point) geom, json);
        } else if (geom instanceof Polygon) {
            serializePolygon((Polygon) geom, json);
        } else if (geom instanceof LineString) {
            serializeLineString((LineString) geom, json);
        } else if (geom instanceof MultiPolygon) {
            serializeMultiPolygon((MultiPolygon) geom, json);
        } else if (geom instanceof MultiPoint) {
            serializeMultiPoint((MultiPoint) geom, json);
        } else if (geom instanceof MultiLineString) {
            serializeMultiLineString((MultiLineString) geom, json);
        } else if (geom instanceof GeometryCollection) {
            serializeGeometryCollection((GeometryCollection) geom, json);
        }

        json.writeEndObject();
    }

    protected void serializeGeometryCollection(final GeometryCollection gc, final JsonGenerator json)
            throws IOException {
        writeTypeField(GEOMETRY_COLLECTION, json);
        json.writeArrayFieldStart("geometries");

        for (final Geometry geom : gc.getGeometries()) {
            serialize(geom, json, null);
        }

        json.writeEndArray();
    }

    protected void serializeMultiLineString(final MultiLineString mls, final JsonGenerator json) throws IOException {
        writeTypeField(MULTI_LINE_STRING, json);
        writeStartCoordinates(json);

        for (final LineString ls : mls.getLines()) {
            json.writeStartArray();
            writePoints(json, ls.getPoints());
            json.writeEndArray();
        }

        writeEndCoordinates(json);
    }

    protected void serializeMultiPoint(final MultiPoint mp, final JsonGenerator json) throws IOException {
        writeTypeField(MULTI_POINT, json);
        writeStartCoordinates(json);
        writePoints(json, mp.getPoints());
        writeEndCoordinates(json);
    }

    protected void serializeMultiPolygon(final MultiPolygon mp, final JsonGenerator json) throws IOException {
        writeTypeField(MULTI_POLYGON, json);
        writeStartCoordinates(json);

        for (final Polygon polygon : mp.getPolygons()) {
            json.writeStartArray();

            for (int i = 0; i < polygon.numRings(); i++) {
                json.writeStartArray();
                writePoints(json, polygon.getRing(i).getPoints());
                json.writeEndArray();
            }

            json.writeEndArray();
        }

        writeEndCoordinates(json);
    }

    protected void serializeLineString(final LineString ls, final JsonGenerator json) throws IOException {
        writeTypeField(LINE_STRING, json);
        writeStartCoordinates(json);
        writePoints(json, ls.getPoints());
        writeEndCoordinates(json);
    }

    protected void serializePolygon(final Polygon polygon, final JsonGenerator json) throws IOException {
        writeTypeField(POLYGON, json);
        writeStartCoordinates(json);

        for (int i = 0; i < polygon.numRings(); i++) {
            json.writeStartArray();
            writePoints(json, polygon.getRing(i).getPoints());
            json.writeEndArray();
        }

        writeEndCoordinates(json);
    }

    protected void serializePoint(final Point point, final JsonGenerator json) throws IOException {
        writeTypeField(POINT, json);
        writeStartCoordinates(json);
        writeNumbers(json, point.getX(), point.getY(), point.getZ());
        writeEndCoordinates(json);
    }

    protected void writeTypeField(final String type, final JsonGenerator json) throws IOException {
        json.writeStringField("type", type);
    }

    protected void writeStartCoordinates(final JsonGenerator json) throws IOException {
        json.writeArrayFieldStart("coordinates");
    }

    protected void writeEndCoordinates(final JsonGenerator json) throws IOException {
        json.writeEndArray();
    }

    protected void writeNumbers(final JsonGenerator json, final double... numbers) throws IOException {
        for (final double number : numbers) {
            json.writeNumber(number);
        }
    }

    protected void writePoints(final JsonGenerator json, final Point[] points) throws IOException {
        for (final Point point : points) {
            json.writeStartArray();
            writeNumbers(json, point.getX(), point.getY(), point.getZ());
            json.writeEndArray();
        }
    }
}

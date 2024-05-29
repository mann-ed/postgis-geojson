package net.postgis.geojson.serializers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import net.postgis.geojson.util.GeometryBuilder;
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
 *
 * @author mayconbordin
 */
class GeometrySerializerTest {
    protected ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        final SimpleModule module = new SimpleModule("MyModule");
        module.addSerializer(Geometry.class, new GeometrySerializer());
        mapper.registerModule(module);
    }

    @Test
    @DisplayName("serializePoint")
    void testSerializePoint() throws Exception {

        final Point  obj      = new Point(125.6, 10.1);

        final String actual   = mapper.writeValueAsString(obj);

        final String expected = "{\"type\": \"Point\",\"coordinates\": [125.6, 10.1, 0.0]}";
        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    @DisplayName("serializeLineString")
    void testSerializeLineString() throws Exception {

        final String     expected = "{\"type\": \"LineString\",\"coordinates\": [ [100.0, 0.0, 0.0], [101.0, 1.0, 0.0] ]}";

        final LineString obj      = new LineString(new Point[] { new Point(100.0, 0.0), new Point(101.0, 1.0) });

        final String     actual   = mapper.writeValueAsString(obj);
        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    @DisplayName("serializePolygon")
    void testSerializePolygon() throws Exception {

        final String  expected = "{\"type\":\"Polygon\",\"coordinates\":"
                + "[[[100.0,0.0,0.0],[101.0,0.0,0.0],[101.0,1.0,0.0]," + "[100.0,1.0,0.0],[100.0,0.0,0.0]]]}";

        final Polygon obj      = GeometryBuilder.createPolygon(new Point[] { new Point(100.0, 0.0),
                new Point(101.0, 0.0), new Point(101.0, 1.0), new Point(100.0, 1.0), new Point(100.0, 0.0) });

        final String  actual   = mapper.writeValueAsString(obj);
        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    @DisplayName("serializeMultiLineString")
    void testSerializeMultiLineString() throws Exception {

        final String          expected = "{\"type\": \"MultiLineString\",\"coordinates\": "
                + "[[[100.0, 0.0, 0.0], [101.0, 0.0, 0.0], [101.0, 1.0, 0.0], [100.0, 1.0, 0.0], [100.0, 0.0, 0.0]]]}";

        final MultiLineString obj      = new MultiLineString(
                new LineString[] { new LineString(new Point[] { new Point(100.0, 0.0), new Point(101.0, 0.0),
                        new Point(101.0, 1.0), new Point(100.0, 1.0), new Point(100.0, 0.0) }) });

        final String          actual   = mapper.writeValueAsString(obj);
        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    @DisplayName("serializeMultiPoint")
    void testSerializeMultiPoint() throws Exception {

        final String     expected = "{\"type\": \"MultiPoint\",\"coordinates\": [ [100.0, 0.0, 0.0], [101.0, 1.0, 0.0] ]}";

        final MultiPoint obj      = new MultiPoint(new Point[] { new Point(100.0, 0.0), new Point(101.0, 1.0) });

        final String     actual   = mapper.writeValueAsString(obj);
        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    @DisplayName("serializeMultiPolygon")
    void testSerializeMultiPolygon() throws Exception {

        final String       expected = "{\"type\": \"MultiPolygon\",\"coordinates\": "
                + "[[[[102.0, 2.0, 0.0], [103.0, 2.0, 0.0], [103.0, 3.0, 0.0], [102.0, 3.0, 0.0], [102.0, 2.0, 0.0]]],"
                + "[[[100.0, 0.0, 0.0], [101.0, 0.0, 0.0], [101.0, 1.0, 0.0], [100.0, 1.0, 0.0], [100.0, 0.0, 0.0]],"
                + "[[100.2, 0.2, 0.0], [100.8, 0.2, 0.0], [100.8, 0.8, 0.0], [100.2, 0.8, 0.0], [100.2, 0.2, 0.0]]]"
                + "]}";

        final MultiPolygon obj      = new MultiPolygon(new Polygon[] {
                new Polygon(
                        new LinearRing[] { new LinearRing(new Point[] { new Point(102.0, 2.0), new Point(103.0, 2.0),
                                new Point(103.0, 3.0), new Point(102.0, 3.0), new Point(102.0, 2.0) }) }),

                new Polygon(new LinearRing[] {
                        new LinearRing(new Point[] { new Point(100.0, 0.0), new Point(101.0, 0.0),
                                new Point(101.0, 1.0), new Point(100.0, 1.0), new Point(100.0, 0.0) }),
                        new LinearRing(new Point[] { new Point(100.2, 0.2), new Point(100.8, 0.2),
                                new Point(100.8, 0.8), new Point(100.2, 0.8), new Point(100.2, 0.2) }) }), });

        final String       actual   = mapper.writeValueAsString(obj);
        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    @DisplayName("serializeGeometryCollection")
    void testSerializegGeometryCollection() throws Exception {

        final String             expected = "{\"type\": \"GeometryCollection\",\"geometries\": ["
                + "{ \"type\": \"Point\", \"coordinates\": [100.0, 0.0, 0.0]},"
                + "{ \"type\": \"LineString\", \"coordinates\": [ [101.0, 0.0, 0.0], [102.0, 1.0, 0.0] ] }" + "]}";

        final GeometryCollection obj      = new GeometryCollection(new Geometry[] { new Point(100.0, 0.0),
                new LineString(new Point[] { new Point(101.0, 0.0), new Point(102.0, 1.0) }) });

        final String             actual   = mapper.writeValueAsString(obj);
        JSONAssert.assertEquals(expected, actual, false);
    }

}

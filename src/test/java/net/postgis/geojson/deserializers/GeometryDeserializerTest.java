package net.postgis.geojson.deserializers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import net.postgis.jdbc.geometry.Geometry;
import net.postgis.jdbc.geometry.GeometryCollection;
import net.postgis.jdbc.geometry.LineString;
import net.postgis.jdbc.geometry.MultiLineString;
import net.postgis.jdbc.geometry.MultiPoint;
import net.postgis.jdbc.geometry.MultiPolygon;
import net.postgis.jdbc.geometry.Point;
import net.postgis.jdbc.geometry.Polygon;

/**
 *
 * @author mayconbordin
 * @author emann
 */
class GeometryDeserializerTest {
    protected ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
        final SimpleModule module = new SimpleModule("MyModule");
        module.addDeserializer(Geometry.class, new GeometryDeserializer());
        mapper.registerModule(module);
    }

    @Test
    @DisplayName("deserializePoint")
    void testDeserializePoint() throws Exception {

        final String json = "{\"type\": \"Point\",\"coordinates\": [125.6, 10.1]}";

        final Point  p    = (Point) mapper.readValue(json, Geometry.class);
        assertThat(p).isNotNull().matches(m -> m.getX() == 125.6).matches(m -> m.getY() == 10.1)
                .matches(m -> m.getZ() == 0.0);
    }

    @Test
    @DisplayName("deserializeLineString")
    void testDeserializeLineString() throws Exception {

        final String     json = "{\"type\": \"LineString\",\"coordinates\": [ [100.0, 0.0], [101.0, 1.0] ]}";

        final LineString p    = (LineString) mapper.readValue(json, Geometry.class);

        assertThat(p).isNotNull();
        assertThat(p.numPoints()).isEqualTo(2);
        assertThat(p.getPoint(0).getX()).isEqualTo(100.0);
        assertThat(p.getPoint(0).getY()).isEqualTo(0.0);
        assertThat(p.getPoint(1).getX()).isEqualTo(101.0);
        assertThat(p.getPoint(1).getY()).isEqualTo(1.0);

    }

    @Test
    @DisplayName("deserializePolygon")
    void testDeserializePolygon() throws Exception {

        final String  json = "{\"type\": \"Polygon\",\"coordinates\": "
                + "[[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]]]}";

        final Polygon p    = (Polygon) mapper.readValue(json, Geometry.class);

        assertThat(p).isNotNull();
        assertThat(p.numRings()).isEqualTo(1);
        assertThat(p.numPoints()).isEqualTo(5);

        assertThat(p.getRing(0).getPoint(0).getX()).isEqualTo(100.0);
        assertThat(p.getRing(0).getPoint(0).getY()).isEqualTo(0.0);

        assertThat(p.getRing(0).getPoint(1).getX()).isEqualTo(101.0);
        assertThat(p.getRing(0).getPoint(1).getY()).isEqualTo(0.0);

        assertThat(p.getRing(0).getPoint(2).getX()).isEqualTo(101.0);
        assertThat(p.getRing(0).getPoint(2).getY()).isEqualTo(1.0);

        assertThat(p.getRing(0).getPoint(3).getX()).isEqualTo(100.0);
        assertThat(p.getRing(0).getPoint(3).getY()).isEqualTo(1.0);

        assertThat(p.getRing(0).getPoint(4).getX()).isEqualTo(100.0);
        assertThat(p.getRing(0).getPoint(4).getY()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("deserializeMultiLineString")
    void testDeserializeMultiLineString() throws Exception {

        final String          json = "{\"type\": \"MultiLineString\",\"coordinates\": "
                + "[[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]]]}";

        final MultiLineString p    = (MultiLineString) mapper.readValue(json, Geometry.class);

        assertThat(p).isNotNull();
        assertThat(p.numLines()).isEqualTo(1);
        assertThat(p.numPoints()).isEqualTo(5);

        assertThat(p.getLine(0).getPoint(0).getX()).isEqualTo(100.0);
        assertThat(p.getLine(0).getPoint(0).getY()).isEqualTo(0.0);

        assertThat(p.getLine(0).getPoint(1).getX()).isEqualTo(101.0);
        assertThat(p.getLine(0).getPoint(1).getY()).isEqualTo(0.0);

        assertThat(p.getLine(0).getPoint(2).getX()).isEqualTo(101.0);
        assertThat(p.getLine(0).getPoint(2).getY()).isEqualTo(1.0);

        assertThat(p.getLine(0).getPoint(3).getX()).isEqualTo(100.0);
        assertThat(p.getLine(0).getPoint(3).getY()).isEqualTo(1.0);

        assertThat(p.getLine(0).getPoint(4).getX()).isEqualTo(100.0);
        assertThat(p.getLine(0).getPoint(4).getY()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("testDeserializeMultiPoint")
    void testDeserializeMultiPoint() throws Exception {

        final String     json = "{\"type\": \"MultiPoint\",\"coordinates\": [ [100.0, 0.0], [101.0, 1.0] ]}";

        final MultiPoint p    = (MultiPoint) mapper.readValue(json, Geometry.class);

        assertThat(p).isNotNull();
        assertThat(p.numPoints()).isEqualTo(2);
        assertThat(p.getPoint(0).getX()).isEqualTo(100.0);
        assertThat(p.getPoint(0).getY()).isEqualTo(0.0);
        assertThat(p.getPoint(1).getX()).isEqualTo(101.0);
        assertThat(p.getPoint(1).getY()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("deserializeMultiPolygon")
    void testDeserializeMultiPolygon() throws Exception {

        final String       json = "{\"type\": \"MultiPolygon\",\"coordinates\": "
                + "[[[[102.0, 2.0], [103.0, 2.0], [103.0, 3.0], [102.0, 3.0], [102.0, 2.0]]],"
                + "[[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]],"
                + "[[100.2, 0.2], [100.8, 0.2], [100.8, 0.8], [100.2, 0.8], [100.2, 0.2]]]" + "]}";

        final MultiPolygon p    = (MultiPolygon) mapper.readValue(json, Geometry.class);

        assertThat(p).isNotNull();
        assertThat(p.numPolygons()).isEqualTo(2);
        assertThat(p.getPolygon(0).numRings()).isEqualTo(1);
        assertThat(p.getPolygon(1).numRings()).isEqualTo(2);
        assertThat(p.numPoints()).isEqualTo(15);

        assertThat(p.getPolygon(0).getRing(0).getPoint(0).getX()).isEqualTo(102.0);
        assertThat(p.getPolygon(0).getRing(0).getPoint(0).getY()).isEqualTo(2.0);

        assertThat(p.getPolygon(0).getRing(0).getPoint(1).getX()).isEqualTo(103.0);
        assertThat(p.getPolygon(0).getRing(0).getPoint(1).getY()).isEqualTo(2.0);

        assertThat(p.getPolygon(0).getRing(0).getPoint(2).getX()).isEqualTo(103.0);
        assertThat(p.getPolygon(0).getRing(0).getPoint(2).getY()).isEqualTo(3.0);

        assertThat(p.getPolygon(0).getRing(0).getPoint(3).getX()).isEqualTo(102.0);
        assertThat(p.getPolygon(0).getRing(0).getPoint(3).getY()).isEqualTo(3.0);

        assertThat(p.getPolygon(0).getRing(0).getPoint(4).getX()).isEqualTo(102.0);
        assertThat(p.getPolygon(0).getRing(0).getPoint(4).getY()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("deserializeGeometryCollection")
    void testDeserializegGeometryCollection() throws Exception {

        final String             json = "{\"type\": \"GeometryCollection\",\"geometries\": ["
                + "{ \"type\": \"Point\", \"coordinates\": [100.0, 0.0]},"
                + "{ \"type\": \"LineString\", \"coordinates\": [ [101.0, 0.0], [102.0, 1.0] ] }" + "]}";

        final GeometryCollection p    = (GeometryCollection) mapper.readValue(json, Geometry.class);

        assertThat(p).isNotNull();
        assertThat(p.numGeoms()).isEqualTo(2);

        assertThat(p.getGeometries()[0].getClass().getSimpleName()).isEqualTo("Point");
        assertThat(((Point) p.getGeometries()[0]).getX()).isEqualTo(100.0);
        assertThat(((Point) p.getGeometries()[0]).getY()).isEqualTo(0.0);

        assertThat(p.getGeometries()[1].getClass().getSimpleName()).isEqualTo("LineString");
        assertThat(((LineString) p.getGeometries()[1]).getPoint(0).getX()).isEqualTo(101.0);
        assertThat(((LineString) p.getGeometries()[1]).getPoint(0).getY()).isEqualTo(0.0);
        assertThat(((LineString) p.getGeometries()[1]).getPoint(1).getX()).isEqualTo(102.0);
        assertThat(((LineString) p.getGeometries()[1]).getPoint(1).getY()).isEqualTo(1.0);
    }

}

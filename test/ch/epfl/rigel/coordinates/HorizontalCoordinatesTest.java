package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.test.Impr;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 24/02/2020
 */
public class HorizontalCoordinatesTest {

    @Test
    void toStringWorks() {
        assertEquals("(az=350.0000°, alt=7.2000°)", HorizontalCoordinates.ofDeg(350, 7.2d).toString());
        assertEquals("(az=123.2300°, alt=-85.4567°)", HorizontalCoordinates.ofDeg(123.23, -85.456723).toString());
    }

    @Test
    void ofWorksOnValidParameters() {
        SplittableRandom random = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            final double lon = random.nextDouble(0, Math.PI * 2);
            final double lat = random.nextDouble(-Math.PI / 2, Math.PI / 2);
            HorizontalCoordinates coordinates = HorizontalCoordinates.of(lon, lat);
            assertEquals(lon, coordinates.lon());
            assertEquals(lat, coordinates.lat());
        }

        HorizontalCoordinates trivial = HorizontalCoordinates.of(0, 0);
        assertEquals(0, trivial.lon());
        assertEquals(0, trivial.lat());

        HorizontalCoordinates untested = HorizontalCoordinates.of(0, Math.PI / 2d);
        assertEquals(Math.PI / 2d, untested.lat(), Impr.C_DELTA);
    }

    @Test
    void ofDegWorksOnValidParameters() {
        SplittableRandom random = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            final double lon = random.nextDouble(0, Math.PI * 2);
            final double lat = random.nextDouble(-Math.PI / 2d, Math.PI / 2d);
            final double lonDeg = Angle.toDeg(lon);
            final double latDeg = Angle.toDeg(lat);
            HorizontalCoordinates coordinates = HorizontalCoordinates.ofDeg(lonDeg, latDeg);
            assertEquals(lon, coordinates.lon(), Impr.C_DELTA);
            assertEquals(lat, coordinates.lat(), Impr.C_DELTA);
        }

        HorizontalCoordinates trivial = HorizontalCoordinates.ofDeg(0, 0);
        assertEquals(0, trivial.lon());
        assertEquals(0, trivial.lat());

        HorizontalCoordinates untested = HorizontalCoordinates.ofDeg(0, 90);
        assertEquals(Math.PI / 2d, untested.lat(), Impr.C_DELTA);
    }

    @Test
    void octantWorks() {
        final String n = "N";
        final String e = "E";
        final String s = "S";
        final String w = "W";
        assertEquals(n, HorizontalCoordinates.ofDeg(0, 0).azOctantName(n, e, s, w));
        assertEquals(n + e, HorizontalCoordinates.ofDeg(45, 0).azOctantName(n, e, s, w));
        assertEquals(e, HorizontalCoordinates.ofDeg(90, 0).azOctantName(n, e, s, w));
        assertEquals(s + e, HorizontalCoordinates.ofDeg(135, 0).azOctantName(n, e, s, w));
        assertEquals(s, HorizontalCoordinates.ofDeg(180, 0).azOctantName(n, e, s, w));
        assertEquals(s + w, HorizontalCoordinates.ofDeg(225, 0).azOctantName(n, e, s, w));
        assertEquals(w, HorizontalCoordinates.ofDeg(270, 0).azOctantName(n, e, s, w));
        assertEquals(n + w, HorizontalCoordinates.ofDeg(315, 0).azOctantName(n, e, s, w));

        assertEquals(n + e, HorizontalCoordinates.ofDeg(22.5d, 0).azOctantName(n, e, s, w));

        assertEquals(n + w, HorizontalCoordinates.ofDeg(335, 0).azOctantName(n, e, s, w));
    }

    @Test
    void angularDistanceToWorks() {
        assertEquals(0.0279d, HorizontalCoordinates.ofDeg(6.5682d, 46.5183d)
                .angularDistanceTo(HorizontalCoordinates.ofDeg(8.5476d, 47.3763d)), Impr.C_DELTA);
    }

}

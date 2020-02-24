package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
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
        assertEquals(Math.PI / 2d, untested.lat(), 10e-4);
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
            assertEquals(lon, coordinates.lon(), 10e-4);
            assertEquals(lat, coordinates.lat(), 10e-4);
        }

        HorizontalCoordinates trivial = HorizontalCoordinates.ofDeg(0, 0);
        assertEquals(0, trivial.lon());
        assertEquals(0, trivial.lat());

        HorizontalCoordinates untested = HorizontalCoordinates.ofDeg(0, 90);
        assertEquals(Math.PI / 2d, untested.lat(), 10e-4);
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

        assertEquals(n + w, HorizontalCoordinates.ofDeg(335, 0).azOctantName(n, e, s, w));
    }

}

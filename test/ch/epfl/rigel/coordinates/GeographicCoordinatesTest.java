package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 23/02/2020
 */
public class GeographicCoordinatesTest {

    @Test
    void ofWorksOnValidParameters() {
        SplittableRandom random = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            final double lon = random.nextDouble(-Math.PI, Math.PI);
            final double lat = random.nextDouble(-Math.PI / 2, Math.PI / 2);
            GeographicCoordinates coordinates = GeographicCoordinates.of(lon, lat);
            assertEquals(lon, coordinates.lon());
            assertEquals(lat, coordinates.lat());
        }

        GeographicCoordinates trivial = GeographicCoordinates.of(0, 0);
        assertEquals(0, trivial.lon());
        assertEquals(0, trivial.lat());

        GeographicCoordinates untested = GeographicCoordinates.of(0, Math.PI / 2d);
        assertEquals(Math.PI / 2d, untested.lat(), 10e-4);
    }

    @Test
    void ofDegWorksOnValidParameters() {
        SplittableRandom random = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            final double lon = random.nextDouble(-Math.PI, Math.PI);
            final double lat = random.nextDouble(-Math.PI / 2d, Math.PI / 2d);
            final double lonDeg = Angle.toDeg(lon);
            final double latDeg = Angle.toDeg(lat);
            GeographicCoordinates coordinates = GeographicCoordinates.ofDeg(lonDeg, latDeg);
            assertEquals(lon, coordinates.lon(), 10e-4);
            assertEquals(lat, coordinates.lat(), 10e-4);
        }

        GeographicCoordinates trivial = GeographicCoordinates.ofDeg(0, 0);
        assertEquals(0, trivial.lon());
        assertEquals(0, trivial.lat());

        GeographicCoordinates untested = GeographicCoordinates.ofDeg(0, 90);
        assertEquals(Math.PI / 2d, untested.lat(), 10e-4);
    }

    @Test
    void ofThrowsOnInvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.of(Math.PI, 0));
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.of(-Math.PI - 10e-4, 0));
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.of(0, Math.PI / 2 + 10e-4));
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.of(0, -Math.PI / 2 - 10e-4));
    }

    @Test
    void ofDegThrowsOnInvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.ofDeg(180, 0));
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.ofDeg(-180 - 10e-4, 0));
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.of(0, 90 + 10e-4));
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.of(0, -90 - 10e-4));
    }

    @Test
    void degMethodsWork() {
        // trivial coordinates
        assertEquals(0, GeographicCoordinates.of(0, 0).lonDeg());
        assertEquals(0, GeographicCoordinates.of(0, 0).lonDeg());
        // reachable borders (i.e. all combinations of the limits of the intervals, except for PI for the longitude)
        assertEquals(90, GeographicCoordinates.of(Math.PI / 2d, 0).lonDeg());
        assertEquals(90, GeographicCoordinates.of(0, Math.PI / 2d).latDeg());
        assertEquals(-180, GeographicCoordinates.of(-Math.PI, 0).lonDeg());
        assertEquals(-90, GeographicCoordinates.of(0, -Math.PI / 2d).latDeg());
        // some particular values
        assertEquals(30, GeographicCoordinates.of(Math.PI / 6d, 0).lonDeg(), 10e-4);
        assertEquals(-60, GeographicCoordinates.of(0, -Math.PI / 3d).latDeg(), 10e-4);
    }

    @Test
    void ofFailsOnInvalidParameters() {
        // the longitude must be smaller than PI
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.of(Math.PI, Math.PI / 2d));
        // the latitude must be smaller than or equal to PI/2
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.of(0, Math.PI / 2d + 10e-4));
        // the longitude must be bigger than or equal to -PI
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.of(-Math.PI - 10e-4, 0));
        // the latitude must be bigger than or equal to -PI/2
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.of(0, -Math.PI - 10e-4));
    }

    @Test
    void toStringWorks() {
        assertEquals("(lon=45.0000°, lat=60.0000°)", GeographicCoordinates.of(Math.PI / 4d, Math.PI / 3d).toString());
        assertEquals("(lon=-32.7326°, lat=75.6846°)", GeographicCoordinates.of(Angle.ofDeg(-32.7326d),
                Angle.ofDeg(75.6846d)).toString());
    }

    @Test
    void isValidMethodsWork() {
        assertFalse(GeographicCoordinates.isValidLatDeg(90 + 10e-4));
        assertFalse(GeographicCoordinates.isValidLonDeg(180 + 10e-4));
        assertFalse(GeographicCoordinates.isValidLatDeg(-90 - 10e-4));
        assertFalse(GeographicCoordinates.isValidLonDeg(-180 - 10e-4));

        assertTrue(GeographicCoordinates.isValidLatDeg(90));
        assertTrue(GeographicCoordinates.isValidLatDeg(-90));
        assertTrue(GeographicCoordinates.isValidLonDeg(-180));

        SplittableRandom random = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            assertTrue(GeographicCoordinates.isValidLatDeg(random.nextInt(-90, 90)));
            assertTrue(GeographicCoordinates.isValidLonDeg(random.nextInt(-180, 180)));
        }
    }

}

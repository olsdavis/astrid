package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.test.Impr;
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
            final double lon = random.nextDouble(-180, 180);
            final double lat = random.nextDouble(-90, 90);
            GeographicCoordinates coordinates = GeographicCoordinates.ofDeg(lon, lat);
            assertEquals(Angle.ofDeg(lon), coordinates.lon(), Impr.C_DELTA);
            assertEquals(Angle.ofDeg(lat), coordinates.lat(), Impr.C_DELTA);
        }

        GeographicCoordinates trivial = GeographicCoordinates.ofDeg(0, 0);
        assertEquals(0, trivial.lon());
        assertEquals(0, trivial.lat());

        GeographicCoordinates untested = GeographicCoordinates.ofDeg(0, 90);
        assertEquals(Math.PI / 2d, untested.lat(), Impr.C_DELTA);
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
            assertEquals(lon, coordinates.lon(), Impr.C_DELTA);
            assertEquals(lat, coordinates.lat(), Impr.C_DELTA);
        }

        GeographicCoordinates trivial = GeographicCoordinates.ofDeg(0, 0);
        assertEquals(0, trivial.lon());
        assertEquals(0, trivial.lat());

        GeographicCoordinates untested = GeographicCoordinates.ofDeg(0, 90);
        assertEquals(Math.PI / 2d, untested.lat(), Impr.C_DELTA);
    }

    @Test
    void ofThrowsOnInvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.ofDeg(180, 0));
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.ofDeg(-180 - Impr.C_DELTA, 0));
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.ofDeg(0, 90 + Impr.C_DELTA));
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.ofDeg(0, -90 - Impr.C_DELTA));
    }

    @Test
    void ofDegThrowsOnInvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.ofDeg(180, 0));
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.ofDeg(-180 - Impr.C_DELTA, 0));
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.ofDeg(0, 90 + Impr.C_DELTA));
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.ofDeg(0, -90 - Impr.C_DELTA));
    }

    @Test
    void degMethodsWork() {
        // trivial coordinates
        assertEquals(0, GeographicCoordinates.ofDeg(0, 0).lonDeg());
        assertEquals(0, GeographicCoordinates.ofDeg(0, 0).lonDeg());
        // reachable borders (i.e. all combinations of the limits of the intervals, except for PI for the longitude)
        assertEquals(90, GeographicCoordinates.ofDeg(90, 0).lonDeg());
        assertEquals(90, GeographicCoordinates.ofDeg(0, 90).latDeg());
        assertEquals(-180, GeographicCoordinates.ofDeg(-180, 0).lonDeg());
        assertEquals(-90, GeographicCoordinates.ofDeg(0, -90).latDeg());
        // some particular values
        assertEquals(30, GeographicCoordinates.ofDeg(30, 0).lonDeg(), Impr.C_DELTA);
        assertEquals(-60, GeographicCoordinates.ofDeg(0, -60).latDeg(), Impr.C_DELTA);
    }

    @Test
    void ofFailsOnInvalidParameters() {
        // the longitude must be smaller than PI
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.ofDeg(180, 90));
        // the latitude must be smaller than or equal to PI/2
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.ofDeg(0, 90 + Impr.C_DELTA));
        // the longitude must be bigger than or equal to -PI
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.ofDeg(-180 - Impr.C_DELTA, 0));
        // the latitude must be bigger than or equal to -PI/2
        assertThrows(IllegalArgumentException.class, () -> GeographicCoordinates.ofDeg(0, -90 - Impr.C_DELTA));
    }

    @Test
    void toStringWorks() {
        assertEquals("(lon=45.0000°, lat=60.0000°)", GeographicCoordinates.ofDeg(45, 60).toString());
        assertEquals("(lon=-32.7326°, lat=75.6846°)", GeographicCoordinates.ofDeg(-32.7326d, 75.6846d).toString());
    }

    @Test
    void isValidMethodsWork() {
        assertFalse(GeographicCoordinates.isValidLatDeg(90 + Impr.C_DELTA));
        assertFalse(GeographicCoordinates.isValidLonDeg(180 + Impr.C_DELTA));
        assertFalse(GeographicCoordinates.isValidLatDeg(-90 - Impr.C_DELTA));
        assertFalse(GeographicCoordinates.isValidLonDeg(-180 - Impr.C_DELTA));

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

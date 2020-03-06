package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.test.Impr;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 23/02/2020
 */
public class LEclipticCoordinatesTest {

    @Test
    void ofWorksOnValidParameters() {
        SplittableRandom random = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            final double lon = random.nextDouble(0, 2 * Math.PI);
            final double lat = random.nextDouble(-Math.PI / 2, Math.PI / 2);
            EclipticCoordinates coordinates = EclipticCoordinates.of(lon, lat);
            assertEquals(lon, coordinates.lon());
            assertEquals(lat, coordinates.lat());
        }

        EclipticCoordinates trivial = EclipticCoordinates.of(0, 0);
        assertEquals(0, trivial.lon());
        assertEquals(0, trivial.lat());

        EclipticCoordinates untested = EclipticCoordinates.of(0, Math.PI / 2d);
        assertEquals(Math.PI / 2d, untested.lat(), Impr.C_DELTA);
    }

    @Test
    void ofThrowsOnInvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> EclipticCoordinates.of(2 * Math.PI, 0));
        assertThrows(IllegalArgumentException.class, () -> EclipticCoordinates.of(-Math.PI - Impr.C_DELTA, 0));
        assertThrows(IllegalArgumentException.class, () -> EclipticCoordinates.of(0, Math.PI / 2 + Impr.C_DELTA));
        assertThrows(IllegalArgumentException.class, () -> EclipticCoordinates.of(0, -Math.PI / 2 - Impr.C_DELTA));
    }

    @Test
    void degMethodsWork() {
        // trivial coordinates
        assertEquals(0, EclipticCoordinates.of(0, 0).lonDeg());
        assertEquals(0, EclipticCoordinates.of(0, 0).lonDeg());
        // reachable borders (i.e. all combinations of the limits of the intervals, except for PI for the longitude)
        assertEquals(90, EclipticCoordinates.of(Math.PI / 2d, 0).lonDeg());
        assertEquals(90, EclipticCoordinates.of(0, Math.PI / 2d).latDeg());
        // some particular values
        assertEquals(30, EclipticCoordinates.of(Math.PI / 6d, 0).lonDeg(), Impr.C_DELTA);
    }

    @Test
    void toStringWorks() {
        assertEquals("(\u03BB=45.0000°, \u03B2=60.0000°)", EclipticCoordinates.of(Math.PI / 4d, Math.PI / 3d).toString());
        assertEquals("(\u03BB=32.7326°, \u03B2=75.6846°)", EclipticCoordinates.of(Angle.ofDeg(32.7326d),
                Angle.ofDeg(75.6846d)).toString());
    }
}

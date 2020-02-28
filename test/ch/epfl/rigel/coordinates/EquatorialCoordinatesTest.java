package ch.epfl.rigel.coordinates;

import ch.epfl.test.Impr;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 24/02/2020
 */
public class EquatorialCoordinatesTest {

    @Test
    void ofWorksOnValidParameters() {
        SplittableRandom random = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            final double ra = random.nextDouble(0, Math.PI * 2);
            final double dec = random.nextDouble(-Math.PI / 2, Math.PI / 2);
            EquatorialCoordinates coordinates = EquatorialCoordinates.of(ra, dec);
            assertEquals(ra, coordinates.ra());
            assertEquals(dec, coordinates.dec());
        }

        EquatorialCoordinates trivial = EquatorialCoordinates.of(0, 0);
        assertEquals(0, trivial.ra());
        assertEquals(0, trivial.dec());

        EquatorialCoordinates untested = EquatorialCoordinates.of(0, Math.PI / 2d);
        assertEquals(Math.PI / 2d, untested.dec(), Impr.DELTA);
    }

    @Test
    void degMethodsWork() {
        // trivial coordinates
        assertEquals(0, EquatorialCoordinates.of(0, 0).raDeg());
        assertEquals(0, EquatorialCoordinates.of(0, 0).decDeg());
        // reachable borders (i.e. all combinations of the limits of the intervals, except for PI for the right ascension)
        assertEquals(90, EquatorialCoordinates.of(Math.PI / 2d, 0).raDeg());
        assertEquals(90, EquatorialCoordinates.of(0, Math.PI / 2d).decDeg());
        // some particular values
        assertEquals(30, EquatorialCoordinates.of(Math.PI / 6d, 0).raDeg(), Impr.DELTA);
    }

    @Test
    void hrMethodWorks() {
        // trivial coordinates
        assertEquals(0, EquatorialCoordinates.of(0, 0).raHr());
        // reachable borders
        assertEquals(12, EquatorialCoordinates.of(Math.PI, 0).raHr(), Impr.DELTA);
        // some particular values
        assertEquals(6, EquatorialCoordinates.of(Math.PI / 2, 0).raHr());
        assertEquals(3, EquatorialCoordinates.of(Math.PI / 4, 0).raHr());
        assertEquals(4, EquatorialCoordinates.of(Math.PI / 3, 0).raHr());
    }

    @Test
    void toStringWorks() {
        assertEquals("(ra=4.0000h, dec=60.0000°)",
                EquatorialCoordinates.of(Math.PI / 3, Math.PI / 3).toString());
        assertEquals("(ra=0.0000h, dec=10.0000°)",
                EquatorialCoordinates.of(0, Math.PI / 18).toString());
        assertEquals("(ra=6.0000h, dec=15.0000°)",
                EquatorialCoordinates.of(Math.PI / 2, Math.PI / 12).toString());
    }

}

package ch.epfl.rigel.coordinates;

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
            final double lon = random.nextDouble(0, Math.PI * 2);
            final double lat = random.nextDouble(-Math.PI / 2, Math.PI / 2);
            EquatorialCoordinates coordinates = EquatorialCoordinates.of(lon, lat);
            assertEquals(lon, coordinates.lon());
            assertEquals(lat, coordinates.lat());
        }

        EquatorialCoordinates trivial = EquatorialCoordinates.of(0, 0);
        assertEquals(0, trivial.lon());
        assertEquals(0, trivial.lat());

        EquatorialCoordinates untested = EquatorialCoordinates.of(0, Math.PI / 2d);
        assertEquals(Math.PI / 2d, untested.lat(), 10e-4);
    }
}

package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 12/03/2020
 */
public class LMoonTest {

    @Test
    void constructorThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Moon(EquatorialCoordinates.of(0, 0),
                0, 0, -TestRandomizer.newRandom().nextInt() - 1));
    }

    @Test
    void infoWorks() {
        assertEquals("Lune (37.5%)", new Moon(EquatorialCoordinates.of(0, 0), 0, 0, 0.3752f).info());
        assertEquals("Lune (0.0%)", new Moon(EquatorialCoordinates.of(0, 0), 0, 0, 0).info());

        SplittableRandom random = TestRandomizer.newRandom();
        DecimalFormat format = new DecimalFormat("#0.0");
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            final float v =  (float) random.nextDouble(0, 1);
            double d = v * 1000;
            d = Math.round(d);
            d /= 10;
            assertEquals("Lune (" + format.format(d) + "%)",
                    new Moon(EquatorialCoordinates.of(0, 0), 0, 0, v).info());
        }
    }

}

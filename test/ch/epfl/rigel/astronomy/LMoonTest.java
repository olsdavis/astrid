package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

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
        assertEquals("Lune (37.5%)", new Moon(EquatorialCoordinates.of(0, 0), 0, 0, 0.3752f).toString());
        assertEquals("Lune (0.0%)", new Moon(EquatorialCoordinates.of(0, 0), 0, 0, 0).toString());
    }

}

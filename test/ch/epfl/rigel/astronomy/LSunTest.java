package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 12/03/2020
 */
public class LSunTest {

    @Test
    void defaultValuesSetCorrectly() {
        assertEquals("Soleil", new Sun(EclipticCoordinates.of(0, 0),
                EquatorialCoordinates.of(0, 0), 1f, 0).name());
        assertEquals(-26.7d, new Sun(EclipticCoordinates.of(0, 0),
                EquatorialCoordinates.of(0, 0), 1f, 0).magnitude(), 10e-5);
    }

    @Test
    void throwsOnIncorrectParameters() {
        assertThrows(NullPointerException.class, () -> new Sun(null, EquatorialCoordinates.of(0, 0), 0, 0));
    }

}

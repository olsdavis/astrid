package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 19/03/2020
 */
public class LStarTest {

    @Test
    void constructorThrows() {
        EquatorialCoordinates foo = EquatorialCoordinates.of(0, 0);
        assertThrows(IllegalArgumentException.class, () -> new Star(-1, "", foo, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Star(0, "", foo, 1, 6f));
    }

    @Test
    void defaultValues() {
        assertEquals(0, new Star(0, "", EquatorialCoordinates.of(0, 0), 0, 0).angularSize());
    }

}

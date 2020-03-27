package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 19/03/2020
 */
public class LAsterismTest {

    @Test
    void constructorFails() {
        assertThrows(IllegalArgumentException.class, () -> new Asterism(null));
        assertThrows(IllegalArgumentException.class, () -> new Asterism(List.of()));
    }

    @Test
    void getterWorks() {
        List<Star> stars = List.of(new Star(1, "Hello World", EquatorialCoordinates.of(0, 0), -1, -0.03f),
                new Star(2, "Goodbye bro", EquatorialCoordinates.of(0, 0), 1, -0.1f));
        assertEquals(stars, new Asterism(stars).stars());
    }

}

package ch.epfl.rigel.astronomy;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 19/03/2020
 */
public class LAsterismTest {

    @Test
    void constructorThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Asterism(null));
        assertThrows(IllegalArgumentException.class, () -> new Asterism(List.of()));
    }

}

package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 07/03/2020
 */
public class LCartesianCoordinatesTest {

    @Test
    void toStringWorks() {
        assertEquals("(x=10.0000, y=4.0000)", CartesianCoordinates.of(10, 4).toString());
        assertEquals("(x=0.0000, y=1254.7469)", CartesianCoordinates.of(0, 1254.7469d).toString());
    }
}

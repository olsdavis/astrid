package ch.epfl.coordinates;

import ch.epfl.rigel.coordinates.GeographicalCoordinates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 23/02/2020
 **/
public class GeographicalCoordinatesTest {

    @Test
    public void ofWorksOnValidParameters() {
        var test = GeographicalCoordinates.of(1.49572210, 0.716962115);
        assertEquals(1.49572210, test.lon());
        assertEquals(0.716962115, test.lat());
        test = GeographicalCoordinates.of(Math.PI / 3, Math.PI / 18);
        assertEquals(Math.PI / 3, test.lon());
        assertEquals(Math.PI / 18, test.lat());
        test = GeographicalCoordinates.of(0, 0);
        assertEquals(0, test.lon());
        assertEquals(0, test.lat());
    }

    @Test
    public void ofFailsOnInvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            GeographicalCoordinates.of(Math.PI, Math.PI / 2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            GeographicalCoordinates.of(2 * Math.PI, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            GeographicalCoordinates.of(0, 2 * Math.PI);
        });
    }

    @Test
    public void toStringWorksOnValidInstance() {
        assertEquals("(lon=45.0000°, lat=60°)", GeographicalCoordinates.of(Math.PI / 4, Math.PI / 3).toString());
    }
}

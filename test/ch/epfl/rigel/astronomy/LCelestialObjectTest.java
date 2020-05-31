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
public class LCelestialObjectTest {

    @Test
    void constructorThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new CelestialObject("Test", EquatorialCoordinates.of(0, 0),
                        -TestRandomizer.newRandom().nextInt(0, Integer.MAX_VALUE) - 1, 0){
                    @Override
                    public Type getType() {
                        return null;
                    }
                });
        assertThrows(NullPointerException.class, () ->
                new CelestialObject(null, EquatorialCoordinates.of(0, 0),
                        0, 0){
                    @Override
                    public Type getType() {
                        return null;
                    }
                });
        assertThrows(NullPointerException.class, () ->
                new CelestialObject("Test", null, 0, 0){
                    @Override
                    public Type getType() {
                        return null;
                    }
                });
    }

    @Test
    void rightDefaults() {
        CelestialObject object = new CelestialObject("Test", EquatorialCoordinates.of(0, 0), 0, 0) {
            @Override
            public Type getType() {
                return null;
            }
        };
        assertEquals(object.info(), object.toString(), "wrong default value");
        assertEquals(object.name(), object.info(), "wrong default value");
    }

}

package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * TODO
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 02/03/2020
 */
public class EquatorialToHorizontalConversionTest {

    @Test
    void equatorialToHorizontalConversionWorks() {
        EquatorialToHorizontalConversion conv = new EquatorialToHorizontalConversion(ZonedDateTime.now(ZoneOffset.UTC), EquatorialCoordinates.of())
    }
}

package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TODO
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 02/03/2020
 */
public class EclipticToEquatorialConversionTest {

    @Test
    void conversionWorksOnTrivialCoordinates() {
        EclipticToEquatorialConversion e = new EclipticToEquatorialConversion(ZonedDateTime.now());
        EclipticCoordinates coordinates = EclipticCoordinates.of(0d, 0d);
        assertEquals(EquatorialCoordinates.of(0d, 0d).lat(), e.apply(coordinates).lat());
        assertEquals(EquatorialCoordinates.of(0d, 0d).lon(), e.apply(coordinates).lon());
    }

    @Test
    void conversionWorks() {
        EclipticToEquatorialConversion e = new EclipticToEquatorialConversion(ZonedDateTime.now());
    }

}

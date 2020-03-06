package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 02/03/2020
 */
public class LEquatorialToHorizontalConversionTest {

    @Test
    void conversionWorksOnTrivialCases() {
        EquatorialCoordinates coordinates1 = EquatorialCoordinates.of(0,0);
        EquatorialToHorizontalConversion system = new EquatorialToHorizontalConversion(ZonedDateTime.of(LocalDate.of(2000, Month.JANUARY, 1),
                LocalTime.of(12, 0),
                ZoneOffset.UTC),
                GeographicCoordinates.ofDeg(0,0));
        HorizontalCoordinates coordinates2 = system.apply(coordinates1);
        assertEquals(90, coordinates2.azDeg());
        assertEquals(10.46d,coordinates2.altDeg(), 10e-3);
    }

    @Test
    void conversionWorksOnBook() {
        EquatorialCoordinates coordinates1 = EquatorialCoordinates.of(0, Angle.ofDMS(23,13,10));
        EquatorialToHorizontalConversion system = new EquatorialToHorizontalConversion(ZonedDateTime.of(LocalDate.of(2000, Month.JANUARY, 1),
                LocalTime.of(12, 0),
                ZoneOffset.UTC),
                GeographicCoordinates.ofDeg(0,52));
        HorizontalCoordinates coordinates2 = system.apply(coordinates1);
        //assertEquals(283.271027267d, coordinates2.azDeg()); // need to set manually the hour angle
    }

}

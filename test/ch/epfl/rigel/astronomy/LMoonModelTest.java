package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.*;

import static ch.epfl.rigel.astronomy.Epoch.J2010;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 30/03/2020
 */
public class LMoonModelTest {

    @Test
    void atWorks() {
        assertEquals(14.211456457836, MoonModel.MOON.at(-2313, new EclipticToEquatorialConversion(ZonedDateTime
                .of(LocalDate.of(2003,
                        Month.SEPTEMBER,
                        1),
                        LocalTime.of(0, 0), ZoneOffset.UTC))).equatorialPos().raHr(), 1e-12);
        assertEquals(-0.20114171346019355, MoonModel.MOON.at(-2313, new EclipticToEquatorialConversion(ZonedDateTime
                .of(LocalDate.of(2003,
                        Month.SEPTEMBER,
                        1),
                        LocalTime.of(0, 0), ZoneOffset.UTC))).equatorialPos().dec(), 1e-13);
        assertEquals(0.009225908666849136, MoonModel.MOON.at(J2010.daysUntil(ZonedDateTime.of(LocalDate.of(1979, 9, 1), LocalTime.of(0, 0),
                ZoneOffset.UTC)), new EclipticToEquatorialConversion(ZonedDateTime.of(
                LocalDate.of(1979, 9, 1), LocalTime.of(0, 0), ZoneOffset.UTC))).angularSize());
        assertEquals("Lune (22.5%)", MoonModel.MOON.at(J2010.daysUntil(ZonedDateTime.of(LocalDate.of(2003, 9, 1), LocalTime.of(0, 0),
                ZoneOffset.UTC)), new EclipticToEquatorialConversion(ZonedDateTime.of(LocalDate.of(2003, 9, 1),
                LocalTime.of(0, 0), ZoneOffset.UTC))).info());
        assertEquals(Angle.ofDeg(214.862515), MoonModel.MOON.at(-2313, new EclipticToEquatorialConversion(ZonedDateTime.of(LocalDate.of(2003, 9, 1),
                LocalTime.of(0, 0),
                ZoneOffset.UTC))).equatorialPos().ra());
        assertEquals(1.716257, MoonModel.MOON.at(-2313, new EclipticToEquatorialConversion(ZonedDateTime.of(LocalDate.of(2003, 9, 1),
                LocalTime.of(0, 0),
                ZoneOffset.UTC))).equatorialPos().decDeg());
    }

}

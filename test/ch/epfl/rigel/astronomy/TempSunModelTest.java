package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import org.junit.jupiter.api.Test;

import java.time.*;

import static ch.epfl.rigel.astronomy.Epoch.J2010;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 26/03/2020
 */
public class TempSunModelTest {

    @Test
    void testAt() {
        assertEquals(5.9325494700300885, SunModel.SUN.at(27 + 31,
                new EclipticToEquatorialConversion(ZonedDateTime.of(LocalDate.of(2010, Month.FEBRUARY, 27),
                        LocalTime.of(0, 0), ZoneOffset.UTC))).equatorialPos().ra());
        assertEquals(19.35288373097352, SunModel.SUN.at(-2349, new EclipticToEquatorialConversion(ZonedDateTime.of(LocalDate.of(2003, Month.JULY, 27),
                LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC))).equatorialPos().decDeg());
        assertEquals(8.3926828082978, SunModel.SUN.at(-2349, new EclipticToEquatorialConversion(ZonedDateTime.of(LocalDate.of(2003, Month.JULY, 27),
                LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC))).equatorialPos().raHr(), 1e-14);
        ZonedDateTime zdt = ZonedDateTime.of(LocalDate.of(1988, Month.JULY, 27), LocalTime.of(0, 0), ZoneOffset.UTC);
        assertEquals(0.3353207024580374, SunModel.SUN.at(J2010.daysUntil(zdt),
                new EclipticToEquatorialConversion(zdt)).equatorialPos().dec());
        ZonedDateTime zone1988 = ZonedDateTime.of(
                LocalDate.of(1988, Month.JULY, 27),
                LocalTime.of(0, 0), ZoneOffset.UTC);
        assertEquals(SunModel.SUN.at(J2010.daysUntil(zone1988), new EclipticToEquatorialConversion(zone1988)).angularSize(), 0.009162353351712227, 1e-14);
    }

}

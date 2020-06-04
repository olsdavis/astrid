package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 03/06/2020
 */
public class SunRiseSetTest {

    @Test
    void sunriseTest() {

        ZonedDateTime book = ZonedDateTime.of(
                LocalDate.of(2010, 8, 24),
                LocalTime.of(12, 12, 12),
                ZonedDateTime.now().getZone());
        double days = Epoch.J2010.daysUntil(book);
        Sun sun = SunModel.SUN.at(days, new EclipticToEquatorialConversion(book));
        GeographicCoordinates positionLauz = GeographicCoordinates.ofDeg(6.6667, 46.5333);
        LocalTime sunriseLauz = SunRiseSet.sunrise(positionLauz, ZonedDateTime.now());
        LocalTime realSunriseLauz = LocalTime.of(5, 43);

        GeographicCoordinates positionBook = GeographicCoordinates.ofDeg(64, 30);
        LocalTime sunriseBook = SunRiseSet.sunrise(positionBook, book);
        LocalTime realSunriseBook = LocalTime.of(14, 16, 0);

        //assertEquals(LocalTime.of(0, 0, 0), sunriseBook);
        assertEquals(realSunriseLauz.getHour(), sunriseLauz.getHour());
        assertEquals(realSunriseLauz.getMinute(), sunriseLauz.getMinute());
    }
}

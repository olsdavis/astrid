package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 02/03/2020
 */
public class LSiderealTimeTest {

    @Test
    void greenwichWorks() {
        assertEquals(Angle.ofHr(4.668119327d), SiderealTime.greenwich(
                ZonedDateTime.of(LocalDate.of(1980, Month.APRIL, 22),
                        LocalTime.of(14, 36, 51, (int) 6.7e8),
                        ZoneOffset.UTC)),
                10e-11
        );
    }

    @Test
    void localWorks() {
        assertEquals(1.74570958832716d, SiderealTime.local(ZonedDateTime.of(LocalDate.of(1980, Month.APRIL, 22),
                LocalTime.of(14, 36, 51, 270_000_000), ZoneOffset.UTC),
                GeographicCoordinates.ofDeg(30, 45)), 10e-4);
    }

}

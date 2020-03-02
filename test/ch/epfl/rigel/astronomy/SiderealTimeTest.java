package ch.epfl.rigel.astronomy;

import static org.junit.jupiter.api.Assertions.*;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.*;

/**
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 02/03/2020
 */
public class SiderealTimeTest {

    @Test
    void greenwichWorks() {
        assertEquals(Angle.ofHr(4.668119327d), SiderealTime.greenwich(
                ZonedDateTime.of(LocalDate.of(1980, Month.APRIL, 22),
                        LocalTime.of(14, 36, 51, (int) 6.7e8),
                        ZoneOffset.UTC)),
                10e-11
        );
    }

}

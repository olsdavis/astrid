package ch.epfl.rigel.astronomy;

import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TODO
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 02/03/2020
 */
public class EpochTest {

    @Test
    void daysUntilWorks() {
        double time = ChronoUnit.DAYS.between(ZonedDateTime.of(LocalDate.of(2000, Month.JANUARY, 1), LocalTime.of(12, 0,0), ZoneOffset.UTC), ZonedDateTime.now());
        //assertEquals(time, Epoch.J2000.daysUntil(ZonedDateTime.now()), 10e-3);
        assertEquals(ZonedDateTime.now().getDayOfYear()+10*365+2 + ZonedDateTime.now().getHour()/24d, Epoch.J2010.daysUntil(ZonedDateTime.now()),10e-2);
        ZonedDateTime zone = ZonedDateTime.of(LocalDate.of(2018, Month.MARCH, 4), LocalTime.MIN, ZoneId.systemDefault());
        double res = zone.getDayOfYear()+8*365+2+ zone.getHour()/24d+zone.getMinute()/(24d*60d)+zone.getSecond()/(24d*60d*60d);
        assertEquals(res, Epoch.J2010.daysUntil(zone), 10e-2);
        ZonedDateTime d = ZonedDateTime.of(
                LocalDate.of(2000, Month.JANUARY, 3),
                LocalTime.of(18, 0),
                ZoneOffset.UTC);
        assertEquals(2.25d, Epoch.J2000.daysUntil(d));
        assertEquals(0, Epoch.J2000.daysUntil(ZonedDateTime.of(LocalDate.of(2000, Month.JANUARY, 1), LocalTime.NOON, ZoneOffset.UTC)));
        assertEquals(res+10*365+2-0.5d, Epoch.J2000.daysUntil(zone), 10e-2);
    }

    @Test
    void julianCenturiesWorks() {
        assertEquals(0, Epoch.J2000.julianCenturiesUntil(ZonedDateTime.of(LocalDate.of(2000, Month.JANUARY, 1), LocalTime.NOON, ZoneOffset.UTC)));
        assertEquals(2.25d/36525d, Epoch.J2000.julianCenturiesUntil(ZonedDateTime.of(
                LocalDate.of(2000, Month.JANUARY, 3),
                LocalTime.of(18, 0),
                ZoneOffset.UTC)));
    }
}

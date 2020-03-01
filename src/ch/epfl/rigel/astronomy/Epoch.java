package ch.epfl.rigel.astronomy;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * Represents astronomic epochs.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 01/03/2020
 */
public enum Epoch {

    /**
     * Represents the epoch starting on the 1st of January 2000, at 12:00.
     */
    J2000(ZonedDateTime.of(
            LocalDate.of(2000, Month.JANUARY, 1),
            LocalTime.of(12, 0),
            ZoneOffset.UTC)
    ),
    /**
     * Represents the epoch starting on the 31st of January 2009, at 0:00.
     */
    J2010(ZonedDateTime.of(
            LocalDate.of(2010, Month.JANUARY, 1).minusDays(1),
            LocalTime.of(0, 0),
            ZoneOffset.UTC)
    );

    private static final double FROM_MILLIS_TO_DAYS = 1d / (1000 * 3600 * 24);

    private final ZonedDateTime epochStart;

    Epoch(ZonedDateTime epochStart) {
        this.epochStart = epochStart;
    }

    /**
     * @param when the other date
     * @return the number of days between the start of the epoch and the provided date {@code when}.
     */
    double daysUntil(ZonedDateTime when) {
        return epochStart.until(when, ChronoUnit.MILLIS) / 1000d / 3600d / 24d;
    }

    /**
     * @param when the other date
     * @return the number of julian years (365.25 days) between the start of the epoch
     * and the provided date {@code when}.
     */
    double julianCenturiesUntil(ZonedDateTime when) {
        return epochStart.until(when, ChronoUnit.MILLIS) / 1000d / 31_557_600d;
    }

}

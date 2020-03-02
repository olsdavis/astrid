package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Utilities for sidereal times calculations.
 *
 * @author Oscar Davis (SCIPER: 311193)
 * @author Alexandre Doukhan (SCIPER: 316706)
 * Creation date: 01/03/2020
 */
public final class SiderealTime {

    private static final Polynomial S_0 = Polynomial.of(0.000025862d, 2400.051336d, 6.697374558d);
    private static final Polynomial S_1 = Polynomial.of(1.002737909d, 0);

    /**
     * @param when a date
     * @return the greenwich sidereal time of the provided date {@code when} (in radians, in the [0, 2*PI[ interval).
     */
    public static double greenwich(ZonedDateTime when) {
        final ZonedDateTime w = when.withZoneSameInstant(ZoneOffset.UTC);
        final double j = Epoch.J2000.julianCenturiesUntil(w.truncatedTo(ChronoUnit.DAYS));
        final double d = ChronoUnit.MILLIS.between(when.truncatedTo(ChronoUnit.DAYS), when) / 3_600_000d;
        return Angle.normalizePositive(
                Angle.ofHr(S_0.at(j) + S_1.at(d))
        );
    }

    /**
     * @param when a date
     * @param where a position
     * @return the local sidereal time (in radians, in the [0, 2*PI[ interval), according to the provided date
     * {@code when} and the provided position {@code where}.
     */
    public static double local(ZonedDateTime when, GeographicCoordinates where) {
        return Angle.normalizePositive(
                greenwich(when) + Angle.toHr(where.lon())
        );
    }

    private SiderealTime() {
    }

}

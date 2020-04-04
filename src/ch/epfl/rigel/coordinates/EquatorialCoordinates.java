package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

import static ch.epfl.rigel.Preconditions.checkInInterval;

/**
 * Implementation of SphericalCoordinates for the equatorial system of coordinates.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 20/02/2020
 */
public final class EquatorialCoordinates extends SphericalCoordinates {

    /**
     * @param ra  Right ascension in radians. Must be in the interval [0, 2*PI[.
     * @param dec Declination in radians. Must be in the interval [-PI/2, PI/2].
     * @return a new instance of EquatorialCoordinates with given parameters.
     */
    public static EquatorialCoordinates of(double ra, double dec) {
        return new EquatorialCoordinates(checkInInterval(RightOpenInterval.of(0, 2 * Math.PI), ra),
                checkInInterval(ClosedInterval.symmetric(Math.PI), dec));
    }

    /**
     * @param lon the longitude (in radians)
     * @param lat the latitude (in radians)
     */
    private EquatorialCoordinates(double lon, double lat) {
        super(lon, lat);
    }

    /**
     * @return the right ascension in radians.
     */
    public double ra() {
        return super.lon();
    }

    /**
     * @return the right ascension in degrees.
     */
    public double raDeg() {
        return super.lonDeg();
    }

    /**
     * @return the right ascension in hours.
     */
    public double raHr() {
        return Angle.toHr(super.lon());
    }

    /**
     * @return the declination in radians.
     */
    public double dec() {
        return super.lat();
    }

    /**
     * @return the declination in degrees.
     */
    public double decDeg() {
        return super.latDeg();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(ra=%.4fh, dec=%.4f°)", raHr(), decDeg());
    }
}

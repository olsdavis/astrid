package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

import static ch.epfl.rigel.Preconditions.checkInInterval;

/**
 * Implementation of SphericalCoordinates for the equatorial coordinates system.
 *
 * @author Alexandre Doukhan (SCIPER : 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 20/02/2020
 **/
public final class EquatorialCoordinates extends SphericalCoordinates {

    private EquatorialCoordinates(double lon, double lat) {
        super(lon, lat);
    }

    /**
     * Public method to initialize a EquatorialCoordinates instance.
     *
     * @param ra  Right ascension in radians. Must be in the interval [0, 2*PI[.
     * @param dec Declination in radians. Must be in the interval [-PI/2, PI/2[.
     * @return a new instance of EquatorialCoordinates with given parameters.
     */
    public static EquatorialCoordinates of(double ra, double dec) {
        checkInInterval(RightOpenInterval.of(0, 2 * Math.PI), ra);
        checkInInterval(RightOpenInterval.symmetric(Math.PI), dec);
        return new EquatorialCoordinates(ra, dec);
    }

    /**
     * @return the longitude in radians.
     */
    public double ra() {
        return super.lon();
    }

    /**
     * @return the latitude in radians.
     */
    public double dec() {
        return super.lat();
    }

    /**
     * @return the longitude in degrees.
     */
    public double raHr() {
        return Angle.toHr(super.lon());
    }

    /**
     * @return the latitude in degrees.
     */
    public double decDeg() {
        return super.latDeg();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(ra=%.4fh, dec=%.4f°)", raHr(), decDeg());
    }
}

package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.Interval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

import static ch.epfl.rigel.Preconditions.checkInInterval;

/**
 * Implementation of SphericalCoordinates for the ecliptic system of coordinates.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 20/02/2020
 */
public final class EclipticCoordinates extends SphericalCoordinates {

    /**
     * This interval represents the possible values for a longitude (in radians).
     */
    private static final Interval LONGITUDE_INTERVAL = RightOpenInterval.of(0, 2 * Math.PI);
    /**
     * This interval represents the possible values for a latitude (in radians).
     */
    private static final Interval LATITUDE_INTERVAL = ClosedInterval.symmetric(Math.PI);

    /**
     * @param lon longitude in radians. Must be in the interval [0, 2*PI[.
     * @param lat latitude in radians. Must be in the interval [-PI/2, PI/2].
     * @return a new instance of EclipticCoordinates from the given parameters.
     * @throws IllegalArgumentException if {@code lon} or {@code lat} is not in
     *                                  the right interval
     */
    public static EclipticCoordinates of(double lon, double lat) {
        return new EclipticCoordinates(checkInInterval(LONGITUDE_INTERVAL, lon),
                checkInInterval(LATITUDE_INTERVAL, lat));
    }

    /**
     * @param lon the longitude (in radians)
     * @param lat the latitude (in radians)
     */
    private EclipticCoordinates(double lon, double lat) {
        super(lon, lat);
    }

    /**
     * @return the longitude in radians.
     */
    @Override
    public double lon() {
        return super.lon();
    }

    /**
     * @return the latitude in radians.
     */
    @Override
    public double lat() {
        return super.lat();
    }

    /**
     * @return the longitude in degrees.
     */
    @Override
    public double lonDeg() {
        return super.lonDeg();
    }

    /**
     * @return the latitude in degrees.
     */
    @Override
    public double latDeg() {
        return super.latDeg();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(\u03BB=%.4f°, \u03B2=%.4f°)", lonDeg(), latDeg());
    }

}

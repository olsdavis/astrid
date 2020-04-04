package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

import static ch.epfl.rigel.Preconditions.checkInInterval;

/**
 * Implementation of SphericalCoordinates for the geographical system of coordinates.
 *
 * @author Alexandre Doukhan (SCIPER: 316706)
 * @author Oscar Davis (SCIPER: 311193)
 * Creation date: 20/02/2020
 */
public final class GeographicCoordinates extends SphericalCoordinates {

    /**
     * @param lon Longitude in degrees. Must be in the interval {@code [-180, 180[}.
     * @param lat Latitude in degrees. Must be in the interval {@code [-90, 90]}.
     * @return a new instance of GeographicalCoordinates with given parameters.
     */
    public static GeographicCoordinates ofDeg(double lon, double lat) {
        return new GeographicCoordinates(Angle.ofDeg(checkInInterval(RightOpenInterval.symmetric(360), lon)),
                Angle.ofDeg(checkInInterval(ClosedInterval.symmetric(180), lat)));
    }

    /**
     * @param lonDeg Longitude given in degrees.
     * @return true if and only if the parameter is in {@code [-180, 180[}.
     */
    public static boolean isValidLonDeg(double lonDeg) {
        return RightOpenInterval.symmetric(360).contains(lonDeg);
    }

    /**
     * @param latDeg Latitude given in degrees.
     * @return true if and only if the parameter is in {@code [-90, 90]}.
     */
    public static boolean isValidLatDeg(double latDeg) {
        return ClosedInterval.symmetric(180).contains(latDeg);
    }

    /**
     * @param lon the longitude (in radians)
     * @param lat the latitude (in radians)
     */
    private GeographicCoordinates(double lon, double lat) {
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
        return String.format(Locale.ROOT, "(lon=%.4f°, lat=%.4f°)", lonDeg(), latDeg());
    }
}

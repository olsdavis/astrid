package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

import static ch.epfl.rigel.Preconditions.*;

/**
 * Implementation of spherical coordinates for the geographical coordinates system.
 *
 * @author Alexandre Doukhan
 * Creation date: 20/02/2020
 **/
public final class GeographicalCoordinates extends SphericalCoordinates {

    private GeographicalCoordinates(double lon, double lat) {
        super(lon, lat);
    }

    /**
     * Public method to initialize a GeographicalCoordinates instance.
     *
     * @param lon Longitude in degrees. Must be in the interval [-180, 180[.
     * @param lat Latitude in degrees. Must be in the interval [-90, 90[.
     * @return a new instance of GeographicalCoordinates with given parameters.
     */
    public static GeographicalCoordinates of(double lon, double lat) {
        checkInInterval(RightOpenInterval.symmetric(360), lon);
        checkInInterval(RightOpenInterval.symmetric(180), lat);
        return new GeographicalCoordinates(lon, lat);
    }

    /**
     * @param lonDeg Longitude given in degrees.
     * @return true iff the parameter is in [-180, 180[.
     */
    public static boolean isValidLonDeg(double lonDeg) {
        return RightOpenInterval.symmetric(360).contains(lonDeg);
    }

    /**
     * @param latDeg Latitude given in degrees.
     * @return true iff the parameter is in [-90, 90[.
     */
    public static boolean isValidLatDeg(double latDeg) {
        return RightOpenInterval.symmetric(180).contains(latDeg);
    }

    public double lon(){
        return super.lon();
    }

    public double lat(){
        return super.lat();
    }

    public double lonDeg(){
        return super.lonDeg();
    }

    public double latDeg(){
        return super.latDeg();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(lon=%.4f°, lat=%.4f°)", lonDeg(), latDeg());
    }
}

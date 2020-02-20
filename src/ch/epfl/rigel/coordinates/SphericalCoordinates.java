package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

/**
 * Mother class for all spherical coordinates systems used.
 *
 * @author Alexandre Doukhan
 * Creation date: 20/02/2020
 **/
abstract class SphericalCoordinates {
    private double lon;
    private double lat;

    SphericalCoordinates(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;

    }

    /**
     * @return the longitude in radians.
     */
    double lon() {
        return lon;
    }

    /**
     * @return the latitude in radians.
     */
    double lat() {
        return lat;
    }

    /**
     * @return the longitude in degrees.
     */
    double lonDeg() {
        return Angle.toDeg(lon);
    }

    /**
     * @return the latitude in degrees.
     */
    double latDeg() {
        return Angle.toDeg(lat);
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("unsupported operation");
    }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("unsupported operation");
    }
}
